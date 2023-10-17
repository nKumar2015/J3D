import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class J3D extends JPanel{

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 1024;
    private static final double ASPECT_RATIO = ((double) WIDTH)/HEIGHT;
    private static vec3d vCamera = new vec3d(0, 0, 0);
    private static final int FOV = 90;
    private static double rTheta = 0.0;
    private static vec3d vLookDir = new vec3d(0, 0, 1);
    private static double Yaw;

    private static ArrayList<Integer> keyCodes = new ArrayList<Integer>();

    private BufferedImage image;
    private Graphics g;
    private Timer timer;
    private mesh obj;
    private mat4x4 matProj;

    public J3D() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();

        obj = new mesh("./Models/Mountains.obj", Color.RED);
        
        matProj = new mat4x4().make_projectionMatrix(FOV, ASPECT_RATIO, 0.1, 1000.0);

        addKeyListener(new Key());
        setFocusable(true);

        timer = new Timer(10, new TimerListener());
        timer.start();
    }

    private class TimerListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            clearScreen(g, Color.BLACK);

            vec3d vForward = vLookDir.multiply(0.2);

            for(int keyCode : keyCodes){
                if (keyCode == KeyEvent.VK_W) {
                    vCamera = vCamera.add(vForward);
                }
                if (keyCode == KeyEvent.VK_S) {
                    vCamera = vCamera.subtract(vForward);
                }
                if (keyCode == KeyEvent.VK_A) {
                    Yaw -= 0.03;
                }
                if (keyCode == KeyEvent.VK_D) {
                    Yaw += 0.03;
                }

                if(keyCode == KeyEvent.VK_UP){
                    vCamera.y += 0.3;
                }
                if(keyCode == KeyEvent.VK_DOWN){
                    vCamera.y -= 0.3;
                }
                if(keyCode == KeyEvent.VK_LEFT){
                    vCamera.x += 0.3;
                }
                if(keyCode == KeyEvent.VK_RIGHT){
                    vCamera.x -= 0.3;
                }
            }

            //rTheta += 0.005;

            mat4x4 matRotZ = new mat4x4().make_rotMatrixZ(rTheta);
            mat4x4 matRotX = new mat4x4().make_rotMatrixX(rTheta*0.5);

            mat4x4 matTrans = new mat4x4().make_translationMatrix(0.0, 0.0, 20.0);

            mat4x4 matWorld = new mat4x4().identity();
            matWorld = matRotZ.matrixMul4x4(matRotX);
            matWorld = matWorld.matrixMul4x4(matTrans);

            vec3d up = new vec3d(0, 1, 0);
            vec3d vTarget = new vec3d(0, 0, 1);
            mat4x4 matCameraRot = new mat4x4().make_rotMatrixY(Yaw);
            vLookDir = matCameraRot.matrixTimesVector(vTarget);
            vTarget = vCamera.add(vLookDir);

            mat4x4 matCamera = new mat4x4().pointAt(vCamera, vTarget, up);
            mat4x4 matView = matCamera.quickInverse();

            ArrayList<triangle> trisToDraw = new ArrayList<triangle>();

            for(triangle t: obj.tris){

                triangle tTransformed = new triangle();

                tTransformed.points[0] = matWorld.matrixTimesVector(t.points[0]);
                tTransformed.points[1] = matWorld.matrixTimesVector(t.points[1]);
                tTransformed.points[2] = matWorld.matrixTimesVector(t.points[2]);

                vec3d line1 = tTransformed.points[1].subtract(tTransformed.points[0]);
                vec3d line2 = tTransformed.points[2].subtract(tTransformed.points[0]);

                vec3d normal = line1.crossProduct(line2);
                normal.normalize();

                vec3d camDist = tTransformed.points[0].subtract(vCamera);

                if(camDist.dotProduct(normal) <= 0){
                    //Lighting!
                    vec3d lightDir = new vec3d(0, 1, -1);
                    lightDir.normalize();
                    double lightDp = lightDir.dotProduct(normal);

                    triangle tViewed = new triangle();
                    tViewed.points[0] = matView.matrixTimesVector(tTransformed.points[0]);
                    tViewed.points[1] = matView.matrixTimesVector(tTransformed.points[1]);
                    tViewed.points[2] = matView.matrixTimesVector(tTransformed.points[2]);

                    ClipReturn data = new triangle().clipAgaintPlane(new vec3d(0, 0, 0.1), new vec3d(0, 0, 1.0), tViewed);
                    int clippedTriangles = data.numTris;
                    triangle[] clipped = new triangle[] {data.tri1, data.tri2};

                    for(int n = 0; n < clippedTriangles; n++){ 
                        //Project Cube
                        triangle tProjected = new triangle();
                        tProjected.points[0] = matProj.matrixTimesVector(clipped[n].points[0]);
                        tProjected.points[1] = matProj.matrixTimesVector(clipped[n].points[1]);
                        tProjected.points[2] = matProj.matrixTimesVector(clipped[n].points[2]);

                        tProjected.points[0].x *= -1.0f;
                        tProjected.points[1].x *= -1.0f;
                        tProjected.points[2].x *= -1.0f;
                        tProjected.points[0].y *= -1.0f;
                        tProjected.points[1].y *= -1.0f;
                        tProjected.points[2].y *= -1.0f;

                        //Scale points from normalized space to screen space
                        vec3d vOffset = new vec3d(1, 1, 0);
                        tProjected.points[0] = tProjected.points[0].add(vOffset);
                        tProjected.points[1] = tProjected.points[1].add(vOffset);
                        tProjected.points[2] = tProjected.points[2].add(vOffset);
                        tProjected.points[0].x *= 0.5 * WIDTH;  
                        tProjected.points[0].y *= 0.5 * HEIGHT;
                        tProjected.points[1].x *= 0.5 * WIDTH;
                        tProjected.points[1].y *= 0.5 * HEIGHT;
                        tProjected.points[2].x *= 0.5 * WIDTH;
                        tProjected.points[2].y *= 0.5 * HEIGHT;

                        tProjected.luminance = lightDp;
                        tProjected.color = t.color;

                        trisToDraw.add(tProjected);
                    }
                }
            }
            
            trisToDraw.sort(null);

            for(triangle triToRaster : trisToDraw){
                triangle[] clipped = new triangle[2];
                ArrayList<triangle> listTriangles = new ArrayList<triangle>();

                listTriangles.add(triToRaster);
                int newTriangles = 1;
            
                for(int p = 0; p < 4; p++){
                    int trisToAdd = 0;
                    while(newTriangles > 0){
                        triangle test = listTriangles.remove(0);
                        newTriangles--;

                        ClipReturn data;
                        switch (p){
                            case 0:
                                data = new triangle().clipAgaintPlane(new vec3d(0, 0, 0), new vec3d(0, 1, 0), test);
                                trisToAdd = data.numTris;
                                clipped[0] = data.tri1;
                                clipped[1] = data.tri2;
                                break;
                            case 1:
                                data = new triangle().clipAgaintPlane(new vec3d(0, HEIGHT - 1, 0), new vec3d(0, -1, 0), test);
                                trisToAdd = data.numTris;
                                clipped[0] = data.tri1;
                                clipped[1] = data.tri2;
                                break;
                            case 2:
                                data = new triangle().clipAgaintPlane(new vec3d(0, 0, 0), new vec3d(1, 0, 0), test);
                                trisToAdd = data.numTris;
                                clipped[0] = data.tri1;
                                clipped[1] = data.tri2;
                                break;
                            case 3:
                                data = new triangle().clipAgaintPlane(new vec3d(WIDTH - 1, 0, 0), new vec3d(-1, 0, 0), test);
                                trisToAdd = data.numTris;
                                clipped[0] = data.tri1;
                                clipped[1] = data.tri2;
                                break;
                        }

                        for(int w = 0; w < trisToAdd; w++){
                            listTriangles.add(clipped[w]);
                        }
                    }
                    newTriangles = listTriangles.size();
                }

                for(triangle t : listTriangles){
                    drawTriangle(g, t, false);
                }
            }
            repaint();
        }
    }

    public void clearScreen(Graphics g, Color c){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    public void drawTriangle(Graphics g, triangle t, boolean isWireframe){
        Color c = t.color;
        //Apply luminance values and restrict to 0-255
        int red   = (int) (c.getRed()   * t.luminance) > 255 ? 255 : (int) (c.getRed()   * t.luminance) < 0 ? 0: (int) (c.getRed()   * t.luminance);
        int green = (int) (c.getGreen() * t.luminance) > 255 ? 255 : (int) (c.getGreen() * t.luminance) < 0 ? 0: (int) (c.getGreen() * t.luminance);
        int blue  = (int) (c.getBlue()  * t.luminance) > 255 ? 255 : (int) (c.getBlue()  * t.luminance) < 0 ? 0: (int) (c.getBlue()  * t.luminance);

        g.setColor(new Color(red, green, blue));

        Path2D triangle = new Path2D.Double();

        vec3d[] points = t.points;

        triangle.moveTo(points[0].x, points[0].y);
        triangle.lineTo(points[1].x, points[1].y);
        triangle.lineTo(points[2].x, points[2].y);

        triangle.closePath();

        ((Graphics2D) g).fill(triangle);

        if(isWireframe){
            g.setColor(Color.BLACK);
            ((Graphics2D) g).draw(triangle);
        }
    }

    public void paintComponent(Graphics g){
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("J3D");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocation(0, 0);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new J3D());
        frame.setVisible(true);
    }

    public class Key implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            for(int i = 0; i < keyCodes.size(); i++){
                int keyCode = keyCodes.get(i);
                if(keyCode == e.getKeyCode()){
                    return;
                }
            }
            keyCodes.add(e.getKeyCode());
        }

        @Override
        public void keyTyped(KeyEvent e) { }

        @Override
        public void keyReleased(KeyEvent e) { 
            for(int i = 0; i < keyCodes.size(); i++){
                int keyCode = keyCodes.get(i);
                if(keyCode == e.getKeyCode()){
                    keyCodes.remove(i);
                    return;
                }
            }
        }

    }
}



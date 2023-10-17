import java.awt.Color;

class triangle implements Comparable<triangle>{
    public vec3d[] points;
    public double luminance;
    public Color color;

    public triangle(vec3d[] points, Color c){
        this.points = points;
        this.luminance = 0;
        this.color = c;
    }
    
    public triangle(vec3d[] points){
        this.points = points;
        this.luminance = 0;
        this.color = Color.WHITE;
    }

    public triangle(){
        this.points = new vec3d[] {new vec3d(0, 0, 0), new vec3d(0, 0, 0), new vec3d(0, 0, 0)};
        this.luminance = 0;
        this.color = Color.WHITE;
    }

    //Used for deep copying a nother triangle
    public triangle(triangle other){
        vec3d[] p = other.points;
        this.points = new vec3d[] {new vec3d(p[0].x, p[0].y, p[0].z), new vec3d(p[1].x, p[1].y, p[1].z), new vec3d(p[2].x, p[2].y, p[2].z)};
        this.luminance = other.luminance;
        this.color = other.color;
    }

    public ClipReturn clipAgaintPlane(vec3d planeP, vec3d planeN, triangle in){
        planeN.normalize();

        ClipReturn out = new ClipReturn();

        vec3d[] insidePoints = new vec3d[3]; int insidePointCount = 0;
        vec3d[] outsidePoints = new vec3d[3]; int outsidePointCount = 0;

        double d0 = dist(in.points[0], planeN, planeP);
        double d1 = dist(in.points[1], planeN, planeP);
        double d2 = dist(in.points[2], planeN, planeP);

        if(d0 >= 0) {insidePoints[insidePointCount] = in.points[0];  insidePointCount++; }
        else{ outsidePoints[outsidePointCount] = in.points[0]; outsidePointCount++; }

        if(d1 >= 0) {insidePoints[insidePointCount] = in.points[1];  insidePointCount++; }
        else{ outsidePoints[outsidePointCount] = in.points[1]; outsidePointCount++; }

        if(d2 >= 0) {insidePoints[insidePointCount] = in.points[2];  insidePointCount++; }
        else{ outsidePoints[outsidePointCount] = in.points[2]; outsidePointCount++; }

        if(insidePointCount == 0){  out.numTris = 0; }

        else if(insidePointCount == 3){ 
            out.tri1 = new triangle(in);
            out.numTris = 1;
        }

        else if(insidePointCount == 1 && outsidePointCount == 2){
            out.tri1 = new triangle();

            out.tri1.color = in.color;
            out.tri1.luminance = in.luminance;

            out.tri1.points[0] = insidePoints[0];
            out.tri1.points[1] = new vec3d().intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);
            out.tri1.points[2] = new vec3d().intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[1]);

            out.numTris = 1;
        }

        else if(insidePointCount == 2 && outsidePointCount == 1){
            out.tri1 = new triangle();
            out.tri2 = new triangle();

            out.tri1.color = in.color;
            out.tri1.luminance = in.luminance;

            out.tri2.color = in.color;
            out.tri2.luminance = in.luminance;

            out.tri1.points[0] = insidePoints[0];
            out.tri1.points[1] = insidePoints[1];
            out.tri1.points[2] = new vec3d().intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);

            out.tri2.points[0] = insidePoints[1];
            out.tri2.points[1] = out.tri1.points[2];
            out.tri2.points[2] = new vec3d().intersectPlane(planeP, planeN, insidePoints[1], outsidePoints[0]);

            out.numTris = 2;
        }

        return out;
    }

    private double dist(vec3d p, vec3d planeN, vec3d planeP){
        return (planeN.x * p.x + planeN.y * p.y + planeN.z * p.z - planeN.dotProduct(planeP));
    }

    @Override
    public int compareTo(triangle o) {
        double z1 = (this.points[0].z + this.points[1].z + this.points[1].z)/3.0;
        double z2 = (o.points[0].z + o.points[1].z + o.points[1].z)/3.0;

        if(z1 < z2){
            return 1;
        }

        if(z1 > z2){
            return -1;
        }

        return 0;
    }
}
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

class mesh {
    public triangle[] tris;

    public mesh(triangle[] tris){
        this.tris = tris;
    }

    public mesh(String fileName, Color c){
        try{
            boolean success = importMeshFromFile(fileName, c);
            if(!success){
                throw new Exception("Failed to read data from file!");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private boolean importMeshFromFile(String fileName, Color c){
        try{
            File file = new File(fileName);
            Scanner inputFile = new Scanner(file);

            ArrayList<vec3d> vecs = new ArrayList<vec3d>();
            ArrayList<vec3d> faces = new ArrayList<vec3d>();

            while (inputFile.hasNext()){
                String line = inputFile.nextLine();
                
                if(line.startsWith("v")){
                   String[] nums = line.split(" ");
                   vec3d vec = new vec3d(Double.parseDouble(nums[1]), Double.parseDouble(nums[2]), Double.parseDouble(nums[3]));
                   vecs.add(vec);
                }
                if(line.startsWith("f")){
                   String[] nums = line.split(" ");
                   vec3d vec = new vec3d(Double.parseDouble(nums[1]), Double.parseDouble(nums[2]), Double.parseDouble(nums[3]));
                   faces.add(vec);
                }
            }
            inputFile.close();

            tris = new triangle[faces.size()];

            for(int i = 0; i < faces.size(); i++){
                vec3d face = faces.get(i);

                vec3d[] points = {vecs.get((int) face.x-1), vecs.get((int) face.y-1), vecs.get((int) face.z-1)};
                triangle t = new triangle(points, c);

                tris[i] = t;
            }

            return true;

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return false;
    }
}
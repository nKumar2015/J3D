class vec3d {
    public double x;
    public double y;
    public double z;
    public double w;

    public vec3d(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public vec3d(vec3d other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public vec3d() {
    }

    public vec3d add(vec3d o){
        return new vec3d(this.x + o.x, this.y + o.y, this.z + o.z);
    }

    public vec3d subtract(vec3d o){
        return new vec3d(this.x - o.x, this.y - o.y, this.z - o.z); 
    }

    public vec3d multiply(double scalar){
        return new vec3d(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public vec3d divide(double scalar){
        return new vec3d(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    public double length(){
        return Math.sqrt(this.dotProduct(this));
    }

    public vec3d crossProduct(vec3d b){
        vec3d normal = new vec3d(0, 0, 0);

        normal.x = this.y * b.z - this.z * b.y;
        normal.y = this.z * b.x - this.x * b.z;
        normal.z = this.x * b.y - this.y * b.x;

        return normal;
    }

    public double dotProduct(vec3d b){
        double dotProduct = this.x * b.x + this.y * b.y + this.z * b.z;
        return dotProduct;
    }

    public void normalize(){
        double l = this.length();
        this.x /= l;
        this.y /= l;
        this.z /= l;
    }

    public vec3d intersectPlane(vec3d planeP, vec3d planeN, vec3d lineStart, vec3d lineEnd){
        planeN.normalize();
		double planeD = -planeN.dotProduct(planeP);
		double ad = lineStart.dotProduct(planeN);
		double bd = lineEnd.dotProduct(planeN);
		double t = (-planeD - ad) / (bd - ad);
		vec3d lineStartToEnd = lineEnd.subtract(lineStart);
		vec3d lineToIntersect = lineStartToEnd.multiply(t);
		return lineStart.add(lineToIntersect);
    }   
}
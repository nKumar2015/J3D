class mat4x4{
  public double[][] m = new double[4][4];
  
  public vec3d matrixTimesVector(vec3d i){
    vec3d o = new vec3d(0, 0, 0);
    o.x = i.x * m[0][0] + i.y * m[1][0] + i.z * m[2][0] + m[3][0];
    o.y = i.x * m[0][1] + i.y * m[1][1] + i.z * m[2][1] + m[3][1];
    o.z = i.x * m[0][2] + i.y * m[1][2] + i.z * m[2][2] + m[3][2];
    double w = i.x * m[0][3] + i.y * m[1][3] + i.z * m[2][3] + m[3][3];

    if(w > 0){
      o.x /= w;
      o.y /= w;
      o.z /= w;
    }
    
    return o;
  }

  public mat4x4 matrixMul4x4(mat4x4 o){
    mat4x4 matrix = new mat4x4();
    for (int c = 0; c < 4; c++){
        for (int r = 0; r < 4; r++){
            matrix.m[r][c] = this.m[r][0] * o.m[0][c] + this.m[r][1] * o.m[1][c] + this.m[r][2] * o.m[2][c] + this.m[r][3] * o.m[3][c];
        }
    }
    return matrix;
  }

  public mat4x4 identity(){
    mat4x4 matrix = new mat4x4();
    matrix.m[0][0] = 1.0f;
    matrix.m[1][1] = 1.0f;
    matrix.m[2][2] = 1.0f;
    matrix.m[3][3] = 1.0f;
    return matrix;
  }

  public mat4x4 make_rotMatrixX(double angleRad){
    mat4x4 rotMatrixX = new mat4x4();
    rotMatrixX.m[0][0] = 1;
    rotMatrixX.m[1][1] = Math.cos(angleRad);
    rotMatrixX.m[1][2] = Math.sin(angleRad);
    rotMatrixX.m[2][1] = -Math.sin(angleRad);
    rotMatrixX.m[2][2] = Math.cos(angleRad);
    rotMatrixX.m[3][3] = 1;
    return rotMatrixX;
  }

  public mat4x4 make_rotMatrixY(double angleRad){
    mat4x4 rotMatrixY = new mat4x4();
    rotMatrixY.m[0][0] = Math.cos(angleRad);
    rotMatrixY.m[0][2] = Math.sin(angleRad);
    rotMatrixY.m[2][0] = -Math.sin(angleRad);
    rotMatrixY.m[1][1] = 1.0;
    rotMatrixY.m[2][2] = Math.cos(angleRad);
    rotMatrixY.m[3][3] = 1.0;
    return rotMatrixY;
  }

  public mat4x4 make_rotMatrixZ(double angleRad){
    mat4x4 rotMatrixZ = new mat4x4();
    rotMatrixZ.m[0][0] = Math.cos(angleRad);
    rotMatrixZ.m[0][1] = Math.sin(angleRad);
    rotMatrixZ.m[1][0] = -Math.sin(angleRad);
    rotMatrixZ.m[1][1] = Math.cos(angleRad);
    rotMatrixZ.m[2][2] = 1;
    rotMatrixZ.m[3][3] = 1;
    return rotMatrixZ;
  }

  public mat4x4 make_translationMatrix(double x, double y, double z){
    mat4x4 translationMatrix = new mat4x4();
    translationMatrix.m[0][0] = 1.0f;
    translationMatrix.m[1][1] = 1.0f;
    translationMatrix.m[2][2] = 1.0f;
    translationMatrix.m[3][3] = 1.0f;
    translationMatrix.m[3][0] = x;
    translationMatrix.m[3][1] = y;
    translationMatrix.m[3][2] = z;
    return translationMatrix;
  }

  public mat4x4 make_projectionMatrix(double fov, double aspectRation, double zNear, double zFar){
    double fovRad = 1.0 / Math.tan(fov * 0.5f / 180.0f * Math.PI);
    mat4x4 projectionMatrix = new mat4x4();
    projectionMatrix.m[0][0] = aspectRation * fovRad;
    projectionMatrix.m[1][1] = fovRad;
    projectionMatrix.m[2][2] = zFar / (zFar - zNear);
    projectionMatrix.m[3][2] = (-zFar * zNear) / (zFar - zNear);
    projectionMatrix.m[2][3] = 1.0;
    projectionMatrix.m[3][3] = 0.0;
    return projectionMatrix;
  }

  public mat4x4 pointAt(vec3d pos, vec3d target, vec3d up){
    vec3d newForward = target.subtract(pos);
    newForward.normalize();

    vec3d a = newForward.multiply(up.dotProduct(newForward));
    vec3d newUp = up.subtract(a);
    newUp.normalize();

    vec3d newRight = newUp.crossProduct(newForward);

    mat4x4 matrix = new mat4x4();
		matrix.m[0][0] = newRight.x;	  matrix.m[0][1] = newRight.y;	  matrix.m[0][2] = newRight.z;	  matrix.m[0][3] = 0.0f;
		matrix.m[1][0] = newUp.x;		    matrix.m[1][1] = newUp.y;		    matrix.m[1][2] = newUp.z;		    matrix.m[1][3] = 0.0f;
		matrix.m[2][0] = newForward.x;	matrix.m[2][1] = newForward.y;	matrix.m[2][2] = newForward.z;	matrix.m[2][3] = 0.0f;
		matrix.m[3][0] = pos.x;			    matrix.m[3][1] = pos.y;			    matrix.m[3][2] = pos.z;			    matrix.m[3][3] = 1.0f;
		return matrix;
  }

  // Only for Rotation/Translation Matrices
  public mat4x4 quickInverse(){
		mat4x4 matrix = new mat4x4();
		matrix.m[0][0] = m[0][0]; matrix.m[0][1] = m[1][0]; matrix.m[0][2] = m[2][0]; matrix.m[0][3] = 0.0;
		matrix.m[1][0] = m[0][1]; matrix.m[1][1] = m[1][1]; matrix.m[1][2] = m[2][1]; matrix.m[1][3] = 0.0;
		matrix.m[2][0] = m[0][2]; matrix.m[2][1] = m[1][2]; matrix.m[2][2] = m[2][2]; matrix.m[2][3] = 0.0;
		matrix.m[3][0] = -(m[3][0] * matrix.m[0][0] + m[3][1] * matrix.m[1][0] + m[3][2] * matrix.m[2][0]);
		matrix.m[3][1] = -(m[3][0] * matrix.m[0][1] + m[3][1] * matrix.m[1][1] + m[3][2] * matrix.m[2][1]);
		matrix.m[3][2] = -(m[3][0] * matrix.m[0][2] + m[3][1] * matrix.m[1][2] + m[3][2] * matrix.m[2][2]);
		matrix.m[3][3] = 1.0;
		return matrix;
	}

}
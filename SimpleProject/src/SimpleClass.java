
public class SimpleClass {
<<<<<<< HEAD
//this is my changing
=======
>>>>>>> parent of d55b8ca... Revert "testCommit"
	
	public static void main(String args[]){
		SimpleClass instance_A = new SimpleClass();
		instance_A.a();
		instance_A.b();
		instance_A.c();
	}
	
	public void a(){
		b();
		c();
		d();
	}
	
	public void b(){
		a();
		d();
	}

	public void c(){
		c();
	}
	
	public void d(){
		b();
		c();
	}
}

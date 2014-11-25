import java.io.*;
import java.net.*;
import java.util.*;
import java.math.*;
import java.util.regex.*;

public class P2PTCP {

    private static String E, N;
    
    
    public static void main(String[] args) {
	Scanner scan; Thread st=null;
	Socket peerConnectionSocket=null;
	if(args[0].equals("server")){
	    try{
		ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]));
		System.out.println("Waiting for connection...");
		peerConnectionSocket = ss.accept();

		st = new Thread(new StringSender(new PrintWriter(peerConnectionSocket.getOutputStream())));
		st.start();
		scan = new Scanner (peerConnectionSocket.getInputStream());
		String fromSocket;
		while((fromSocket = scan.nextLine())!=null)
			System.out.println(fromSocket);
	    }catch(IOException e) {System.err.println("Server crash");}
	    finally {st.stop();}
	}
	else if(args[0].equals("client")) {
	    try{
		peerConnectionSocket = new Socket(args[1], Integer.parseInt(args[2]));
		

		//st = new Thread(new StringSender(new PrintWriter(peerConnectionSocket.getOutputStream())),publicKey);
		//st.start();
		PrintWriter out= new PrintWriter(peerConnectionSocket.getOutputStream());
		scan = new Scanner (peerConnectionSocket.getInputStream());
		String publicKey;

		if((publicKey = scan.nextLine())!=null)
			System.out.println(publicKey);

		sscanf(publicKey);

		while(true){
		    
		    Scanner input=new Scanner(System.in);
		    int tmp;
		    System.out.print("Enter number: ");
		    tmp = input.nextInt();
		    out.println(encrypt(""+tmp));
		    out.flush();

		}
	
	    }
      	    catch(Exception e) {System.err.println("Client crash");}
	    finally{st.stop();}
	}
    }
  
    public static void sscanf(String publicKey){
	
	Pattern p = Pattern.compile("#(\\d+)#(\\d+)#");
	Matcher m = p.matcher(publicKey);

	while(m.find()){

	    if(m.group().length()!=0){
		E=m.group(1);
		N=m.group(2);
	    }


	}
	System.out.println("E " +E);
	System.out.println("N " +N);
	
	
       
    }
    
    public static String encrypt(String str){

	BigInteger C = new BigInteger(str);
	BigInteger publicE= new BigInteger(E);
	BigInteger publicN= new BigInteger(N);

	C = C.modPow(publicE,publicN);
	
	return C.toString();
	


    }
}


import java.io.*;
import java.net.*;
import java.util.*;
import java.math.*;

public class P2PTCP {

	private static BigInteger d, N;

	public static void main(String[] args) {
		Scanner scan;
		Thread st = null;
		Socket peerConnectionSocket = null;

		BigInteger one = new BigInteger("1");
		BigInteger two = new BigInteger("2");
		BigInteger E = new BigInteger("3");

		boolean foundE = false;
		boolean foundD = false;

		if (args[0].equals("server")) {
			try {
				ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]),
						Integer.parseInt(args[2]));
				BigInteger size = new BigInteger(args[2]);
				//size = size.divide(two);
				size = approxSqrt(size);

				BigInteger b = new BigInteger("" + size);

				System.out.println("size: " + size);
				System.out.println("size: " + size.bitCount());

				int bitLength = b.bitLength();
				System.out.println("BL : " + bitLength);

				BigInteger p1 = generatePrimes(size);
				BigInteger p2 = generatePrimes(size);

				// makes sure that p1 & p2 aren't the same number
				while(p1.equals(p2)) {
					p2 = generatePrimes(size);
				}

				System.out.println("P1: " + p1 + " P2: " + p2);

				N = p1.multiply(p2);

				System.out.println("N " + N);

				BigInteger phi = (p1.subtract(one)).multiply(p2.subtract(one));
				System.out.println("phi:  " + phi);

				while (!foundE) {

					if (phi.gcd(E).equals(one)) {
						foundE = true;
					} else {
						E = E.add(one).add(one);
					}
				}
				System.out.println("e: " + E);

				BigInteger k = new BigInteger("1");
				BigInteger tmp = new BigInteger("0");

				while (!foundD) {

					tmp = k.multiply(phi);
					tmp = tmp.add(one);

					if (!tmp.gcd(E).equals(one))
						foundD = true;
					else
						k = k.add(one);

				}

				System.out.println("k: " + k);

				tmp = tmp.divide(E);

				d = new BigInteger(tmp.toString());

				System.out.println("d: " + d);

				System.out.println("Waiting for connection....");
				peerConnectionSocket = ss.accept();

				// st = new Thread(new StringSender(new
				// PrintWriter(peerConnectionSocket.getOutputStream())));
				// st.start();
				PrintWriter out = new PrintWriter(
						peerConnectionSocket.getOutputStream());
				out.println("#" + E + "#" + N + "#");
				out.flush();
				scan = new Scanner(peerConnectionSocket.getInputStream());
				String fromSocket;
				while ((fromSocket = scan.nextLine()) != null) {
					System.out.println(fromSocket);
					System.out.println(decrypt(fromSocket));
				}

			} catch (IOException e) {
				System.err.println("Server crash");
			} finally {
				st.stop();
			}
		} else if (args[0].equals("client")) {
			try {
				peerConnectionSocket = new Socket("localhost",
						Integer.parseInt(args[1]));

				st = new Thread(new StringSender(new PrintWriter(
						peerConnectionSocket.getOutputStream())));
				st.start();
				scan = new Scanner(peerConnectionSocket.getInputStream());
				String fromSocket;
				while ((fromSocket = scan.nextLine()) != null) {
					System.out.println(fromSocket);

				}

			} catch (Exception e) {
				System.err.println("Client crash");
			} finally {
				st.stop();
			}
		}
	}

	public static String decrypt(String ciphertext) {

		BigInteger msg = new BigInteger(ciphertext);

		msg = msg.modPow(d, N);

		return msg.toString();
	}

	public static BigInteger generatePrimes(BigInteger size) {

		BigInteger prime = new BigInteger("0");

		while (true) {
			prime = BigInteger.probablePrime(size.bitLength(), new Random());
			if (prime.compareTo(size) == 1 && prime.isProbablePrime(15)) {
				System.out.println("Found a suitable prime");
				return prime;
			}
		}
	}

	public BigInteger approxSqrt(BigInteger value) {
		BigInteger square = new BigInteger("0");
		BigInteger i = new BigInteger("0");

		while(true) {

			square = i.multiply(i);
			if(square.compareTo(value) == 1) {
				break;
			}
			i = i.add(two);
		}

		return i;
	}
}

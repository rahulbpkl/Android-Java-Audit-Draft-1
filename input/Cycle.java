public class Cycle {
  private final int balance;
  
  private  final Cycle c = new Cycle();  // Inserted after initialization of required fields

  public Cycle() {
    balance = deposit - 10; // Subtract processing fee
  }
 private static final int deposit = (int) (Math.random() * 100); // Random deposit
  public static void main(String[] args) {
    System.out.println("The account balance is: " + c.balance);
  }
}

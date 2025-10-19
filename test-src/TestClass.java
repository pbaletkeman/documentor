/**
 * A simple test class to verify code analysis functionality
 */
public class TestClass {
    private String name;
    private int age;

    /**
     * Constructor
     * @param name The name
     * @param age The age
     */
    public TestClass(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * Gets the name
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the age
     * @return The age
     */
    public int getAge() {
        return age;
    }

    /**
     * Main method
     * @param args Command line args
     */
    public static void main(String[] args) {
        TestClass test = new TestClass("John", 30);
        System.out.println("Name: " + test.getName());
        System.out.println("Age: " + test.getAge());
    }
}

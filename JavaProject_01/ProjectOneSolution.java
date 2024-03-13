import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProjectOneSolution implements Developers {
    


    Connection connection = null;
    private void createTable() throws SQLException, ClassNotFoundException {
        Connection connection1 = connectToDatabase();
        PreparedStatement statement = connection1.prepareStatement("CREATE TABLE IF NOT EXISTS developers(name Text, age Integer, location Text, skill Text)");
//        String createTable = "CREATE TABLE IF NOT EXISTS developers(name Text, age Integer, location Text, skill Text)";
        statement.execute();
        statement.close();
        closeDatabase();
    }

    private void loadFromTextToDB(String fileName) throws ClassNotFoundException{

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line;

            while( (line = reader.readLine()) != null ){
                String mode = line.substring(0, line.lastIndexOf('#'));
                String[] parts = mode.split(",");
                String name = parts[0].trim();
                int age = Integer.parseInt(parts[1].trim());
                String location = parts[2].trim();
                String skill = parts[3].trim();
                uploadToDB(name, age, location, skill);
            }

        }catch (IOException exception){
            System.out.println(exception.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void uploadToDB(String name, int age, String location, String skill) throws SQLException, ClassNotFoundException{
        Connection conn = connectToDatabase();
        PreparedStatement statement = conn.prepareStatement("INSERT INTO developers(name, age, location, skill) VALUES( ?, ?, ?, ?)");
//        String uploadSQL = String.format("INSERT INTO developers(name, age, location, skill) VALUES('%S', %d, '%s', '%s')", name, age, location, skill);
        statement.setString(1, name);
        statement.setInt(2, age);
        statement.setString(3, location);
        statement.setString(4, skill);
        statement.execute();
        statement.close();
        closeDatabase();
    }

    private Connection connectToDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/developer", "developer", "charles001");
        return connection;
    }

    private void closeDatabase() throws SQLException{
        try{
            if( connection != null ){
                connection.close();
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public ResultSet loadDevelopers() throws ClassNotFoundException {
        ResultSet resultSet = null;
        try{
            createTable();
            loadFromTextToDB("/Users/preciousojonesicloud.com/Desktop/Charles_Owuala/JavaProject_01/project.txt");
            Connection conn = connectToDatabase();
            Statement statement = conn.createStatement();
            String selectStatement = "SELECT * FROM developers";
            resultSet = statement.executeQuery(selectStatement);


        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return resultSet;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        ProjectOneSolution projectOneSolution = new ProjectOneSolution();
        projectOneSolution.loadDevelopers();
    }
}
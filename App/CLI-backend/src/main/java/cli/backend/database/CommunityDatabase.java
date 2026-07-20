package cli.backend.database;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.services.PostService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommunityDatabase {
    String url = "jdbc:postgresql://localhost:5432/cli-backend-db"; // Database details
    String username = "postgres"; // MySQL credentials
    String password = ${POSTGRES-PASSWORD};
    private static CommunityDatabase instance;

    public static CommunityDatabase getInstance(){
        if(instance==null) {
            instance=new CommunityDatabase();
        }
        return  instance;
    }

    public void addCommunity(Community community){
        String sql= """
            INSERT INTO public.communities (created_at, updated_at, name, topic, description, user_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try(Connection conn = DriverManager.getConnection(url,username,password);
            PreparedStatement stm = conn.prepareStatement(sql)){
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            stm.setTimestamp(1,now);
            stm.setTimestamp(2,now);
            stm.setString(3,community.getNickname());
            stm.setString(4,community.getTopic());
            stm.setString(5,community.getDescription());
            stm.setString(6,community.getCommunityCreator());

            stm.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    public boolean deleteCommunity(String communityName) {
        String sql = """
                    DELETE FROM public.communities where name=?
                """;
        try(Connection conn = DriverManager.getConnection(url,username,password);
            PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1,communityName);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected>0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }

    }

    public List<Community> getDBCommunities() {
        List<Community> dbCommunities= new ArrayList<>();

        String sql = """
                    SELECT * from public.communities;
                """;
        try(Connection conn = DriverManager.getConnection(url,username,password);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql)){
            while(rs.next()) {
                String nickname = rs.getString("name");
                String topic = rs.getString("topic");
                String description = rs.getString("description");

                Long id = rs.getLong("id");
                String communityCreator = rs.getString("user_id");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Timestamp updatedAt = rs.getTimestamp("updated_at");


                Community community = new Community(id,nickname,topic,description,communityCreator, createdAt.toLocalDateTime(), updatedAt.toLocalDateTime());
                dbCommunities.add(community);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return dbCommunities;
    }
}

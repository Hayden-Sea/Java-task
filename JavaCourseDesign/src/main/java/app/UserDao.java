package app;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;


public class UserDao {
    private final JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    //登录用户
    public User login(User login_user) {
        try {
            //编写sql
            String sql = "select * from user where username = ? and password = ?";

            User user = template.queryForObject(sql,
                    new BeanPropertyRowMapper<User>(User.class),
                    login_user.getUsername(), login_user.getPassword());
            return user;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    //注册用户
    public int register(User register_user) {
        try {
            String sql = "insert into user values (null ,?,?,0 )";
            int count = template.update(sql, register_user.getUsername(), register_user.getPassword());
            return count;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //更新用户分数
    public void updateScore(String username,int score) {
        String sql = "update user set score = ? where username = ?";
        template.update(sql,score,username);
    }

}


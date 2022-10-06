/**
 * Create by xy
 * 2022-05-16 21:55
 */

package test;

import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class test {
    @Autowired
    private UserService userService;

    @Test
    public void testInsert(){
        //实现新增用户信息
        //INSERT INTO user ( id, name, age, email ) VALUES ( ?, ?, ?, ? )
        User user = new User();
        //user.setId(100L);
        user.setName("张三");
        user.setStatus(1);
        int result = 0;
        System.out.println("result:"+result);
        System.out.println("id:"+user.getId());
    }
}

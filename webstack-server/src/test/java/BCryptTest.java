public class BCryptTest {
    public static void main(String[] args) {
        System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches("123456", "$2a$10$YXnSRtvFtEr5pXtckOiUq.WA47z2QEr/w4lI8q3CGUvHqC8mLTizi"));
        System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches("e10adc3949ba59abbe56e057f20f883e", "$2a$10$YXnSRtvFtEr5pXtckOiUq.WA47z2QEr/w4lI8q3CGUvHqC8mLTizi"));
    }
}

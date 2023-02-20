import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
    public void test(){
        String file = "eadjsakj.jpg";
        String substring = file.substring(file.lastIndexOf("."));
        System.out.println(substring);
    }
}

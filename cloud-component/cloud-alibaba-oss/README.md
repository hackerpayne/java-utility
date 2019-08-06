

https://github.com/alibaba/spring-cloud-alibaba/blob/master/spring-cloud-alibaba-examples/oss-example/readme-zh.md


1、引用
<dependency>
     <groupId>com.alibaba.cloud</groupId>
     <artifactId>spring-cloud-starter-alicloud-oss</artifactId>
 </dependency>
 
 2、配置
spring.cloud.alicloud.access-key=your-ak
spring.cloud.alicloud.secret-key=your-sk
spring.cloud.alicloud.oss.endpoint=***

3、使用
@Service
 public class YourService {
 	@Autowired
 	private OSSClient ossClient;

 	public void saveFile() {
 		// download file to local
 		ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File("pathOfYourLocalFile"));
 	}
 }
 
 
 
 
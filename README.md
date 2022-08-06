#Apache Jena Backend For Chatbot combine Vietnamese Tourism Ontology Using Spring Boot


Cách chạy: 

Nếu dùng Intellij hoặc Eclipse thì run file OntologyApplication.java trong thư mục src

Nếu dùng terminal ubuntu thì ở thư mục Jena_SPARQL chạy lệnh:

mvn spring-boot:run

******

File ontology du lịch: xmlowlv9.owl

Chương trình Java có 2 nhiệm vụ chính:

- Sinh câu hỏi huấn luyện: Gọi API GET http://localhost:8080/data_train   

-> các câu hỏi huấn luyện Rasa sẽ lưu ở thư mục question. 

Thư mục question gồm nhiều file txt, mỗi file ứng với 1 intent


- Chạy các API cho action của chatbot 

GET http://localhost:8080/one_condition  -> Action cho câu hỏi tìm X t/m 1 điều kiện

GET http://localhost:8080/two_conditions -> Action cho câu hỏi tìm X t/m 2 điều kiện

GET http://localhost:8080/one_property -> Action cho câu hỏi tìm 1 thông tin về X cho trước

GET http://localhost:8080/all -> Action cho câu hỏi giới thiệu X

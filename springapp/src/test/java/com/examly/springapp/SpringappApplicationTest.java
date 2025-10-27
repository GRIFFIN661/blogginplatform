package com.examly.springapp;
import com.examly.springapp.model.Blog;
import com.examly.springapp.model.Comment;
import com.examly.springapp.model.Report;
import com.examly.springapp.repository.BlogRepository;
import com.examly.springapp.repository.CommentRepository;
import com.examly.springapp.repository.ReportRepository;
import com.examly.springapp.service.BlogService;
import com.examly.springapp.service.CommentService;
import com.examly.springapp.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringappApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlogRepository blogRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private ReportRepository reportRepo;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    private Blog testBlog;

    @BeforeEach
    public void setup() {
        commentRepo.deleteAll();
        blogRepo.deleteAll();
        reportRepo.deleteAll();

        testBlog = new Blog();
        testBlog.setTitle("Test Blog");
        testBlog.setContent("This is a test blog content.");
        testBlog.setTags(List.of("test", "spring"));
        blogRepo.save(testBlog);
    }

    @Test
    public void SpringBoot_ProjectAnalysisAndUMLDiagram_InstallSpringBootcreateprojectannotationscontrollerservicelayers() {
        assertThat(blogRepo).isNotNull();
        assertThat(commentRepo).isNotNull();
        assertThat(reportRepo).isNotNull();
        assertThat(blogService).isNotNull();
        assertThat(commentService).isNotNull();
        assertThat(reportService).isNotNull();
    }

    @Test
    public void SpringBoot_ProjectAnalysisAndUMLDiagram_BlogServiceSaveAndRetrieveWorks() {
        Blog blog = new Blog();
        blog.setTitle("New Blog");
        blog.setContent("Content for new blog");
        blog.setTags(List.of("new", "entry"));

        blogService.createBlog(blog);
        List<Blog> blogs = blogService.getAllBlogs();
        assertThat(blogs.stream().anyMatch(b -> "New Blog".equals(b.getTitle()))).isTrue();
    }

    @Test
    public void SpringBoot_DevelopCoreAPIsAndBusinessLogic_GetAllBlogsReturnsEmptyWhenNoneExist() throws Exception {
        blogRepo.deleteAll();
        mockMvc.perform(get("/api/blogs"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void SpringBoot_DevelopCoreAPIsAndBusinessLogic_PostNewBlogAndVerifyContent() throws Exception {
        Blog blog = new Blog();
        blog.setTitle("Posted Blog");
        blog.setContent("Posted content");
        blog.setTags(List.of("post", "test"));

        mockMvc.perform(post("/api/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Posted Blog"));

        assertThat(blogRepo.findAll().stream().anyMatch(b -> "Posted Blog".equals(b.getTitle()))).isTrue();
    }

    @Test
    public void SpringBoot_ProjectAnalysisAndUMLDiagram_CommentServiceAddCommentToBlog() {
        Comment comment = new Comment();
        comment.setAuthor("John");
        comment.setText("Nice blog!");

        Comment saved = commentService.addComment(testBlog.getId(), comment);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBlog().getId()).isEqualTo(testBlog.getId());
    }

    @Test
    public void SpringBoot_ProjectAnalysisAndUMLDiagram_AddNestedCommentWithParentId() {
        Comment parent = new Comment();
        parent.setAuthor("Alice");
        parent.setText("Parent comment");
        parent = commentService.addComment(testBlog.getId(), parent);

        Comment reply = new Comment();
        reply.setAuthor("Bob");
        reply.setText("Reply to parent");
        reply.setParentCommentId(parent.getId());

        Comment savedReply = commentService.addComment(testBlog.getId(), reply);
        assertThat(savedReply.getParentCommentId()).isEqualTo(parent.getId());
    }

    @Test
    public void SpringBoot_ProjectAnalysisAndUMLDiagram_SubmitReportSuccessfully() {
        Report report = new Report();
        report.setContentType("Blog");
        report.setContentId(testBlog.getId());
        report.setReason("Spam content");

        Report saved = reportService.submitReport(report);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContentType()).isEqualTo("Blog");
    }

    @Test
    public void SpringBoot_DatabaseAndSchemaSetup_RepositoryLayerBlogFindAll() {
        List<Blog> blogs = blogRepo.findAll();
        assertThat(blogs).hasSize(1);
        assertThat(blogs.get(0).getTitle()).isEqualTo("Test Blog");
    }

    @Test
    public void SpringBoot_DevelopCoreAPIsAndBusinessLogic_ValidateBlogHeadersAndContentType() throws Exception {
        mockMvc.perform(get("/api/blogs")
                        .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void SpringBoot_DatabaseAndSchemaSetup_RepositoryLayerBlogFindAll_WithSameLogic() {
        List<Blog> blogs = blogRepo.findAll();
        assertThat(blogs).hasSize(1);
        assertThat(blogs.get(0).getTitle()).isEqualTo("Test Blog");
    }   
}


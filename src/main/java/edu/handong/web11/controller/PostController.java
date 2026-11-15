package edu.handong.web11.controller;

import edu.handong.web11.domain.Post;
import edu.handong.web11.dto.PostForm;
import edu.handong.web11.service.PostService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PostController {
    private final PostService posts;

    public PostController(PostService posts) {
        this.posts = posts;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String list(Model model) {
        model.addAttribute("posts", posts.all());
        return "posts/list";
    }

    @GetMapping("/posts/new")
    public String form(Model model) {
        model.addAttribute("postForm", new PostForm());
        return "posts/form";
    }

    @PostMapping("/posts") // posts로 전송한 게시글 등록 요청을 처리한다.
    // 제목과 내용을 postform에 담고 선언된 입력 조건을 검사한다.
    public String create(@Valid @ModelAttribute("postForm") PostForm form, BindingResult errors, // 입력값 검증 결과를 받아서 저장 할 수 있을지 판단한다.
                         @RequestParam(name = "files", required = false) List<MultipartFile> files) { // name이 files인 파일에서 여러 파일을 받는다.
        if (errors.hasErrors()) return "posts/form"; // 입력 조건이 만족하지 않으면 저장하지 않고 작성 폼을 돌려준다.
        // 글과 파의 저장을 service가 하고 저장된 게시물의 id를 받는다
        // 파일 목록이 null이면 service가 다시 돌 수 있게 한다.
        Long id = posts.create(form, files == null ? List.of() : files);
        // 저장된 게시글의 id로 상세 페이지 주소를 만들어서 브라우저로 이동
        return "redirect:/posts/" + id;
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", posts.get(id));
        return "posts/detail";
    }

    @GetMapping("/posts/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Post post = posts.get(id);
        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());
        model.addAttribute("postForm", form);
        model.addAttribute("postId", id);
        return "posts/form";
    }

    @PostMapping("/posts/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("postForm") PostForm form, BindingResult errors,
                         Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("postId", id);
            return "posts/form";
        }
        posts.update(id, form);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id) {
        posts.delete(id);
        return "redirect:/posts";
    }
}

package edu.handong.web11.controller;

import edu.handong.web11.domain.Attachment;
import edu.handong.web11.repository.AttachmentRepository;
import edu.handong.web11.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentRepository attachments;
    private final FileStorageService storage;
    public AttachmentController(AttachmentRepository attachments, FileStorageService storage) {
        this.attachments = attachments; this.storage = storage;
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Attachment file = attachments.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "첨부파일이 없습니다.")); // db에서 해당하는 Id의 파일을 찾고 없으면 첨부파일이 없습니다를 반환한다.
        Resource resource = storage.load(file); // 서버에 저장되어 있는 실제 파일을 가르키는 리소스를 가져온다
        String disposition = ContentDisposition.attachment()
            .filename(file.getOriginalFilename(), StandardCharsets.UTF_8)
            .build().toString(); // 파일을 내려받을 때 원래 이름으로 저장되고 한글 파일명도 깨지지 않도록 설정한다.
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
            .body(resource); // 브라우저가 파일을 다운로드할 수 있도록 필요한 설정과 파일 내용을 함께 보내준다.
    }
    // STUDENT TODO: GET /attachments/{id}/view (image preview).
    @GetMapping ("/{id}/view")
    public ResponseEntity<Resource> view (@PathVariable Long id){
        Attachment file = attachments.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "첨부파일이 없습니다."));
        String contentstype = file.getContentType();
        if(!"image/jpeg".equals(contentstype) && !"image/png".equals(contentstype) && !"image/gif".equals(contentstype)){
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"이미지 파일만 미리 보기 가능");
        }

        Resource resource = storage.load(file);
        String disposition = ContentDisposition.inline().filename(file.getOriginalFilename(), StandardCharsets.UTF_8).filename(file.getOriginalFilename(), StandardCharsets.UTF_8).build().toString();

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentstype)).header(HttpHeaders.CONTENT_DISPOSITION, disposition).body(resource);
    }
}

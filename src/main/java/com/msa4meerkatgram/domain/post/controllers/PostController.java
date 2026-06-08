package com.msa4meerkatgram.domain.post.controllers;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.post.requests.PostIndexReq;
import com.msa4meerkatgram.domain.post.requests.PostUploadReq;
import com.msa4meerkatgram.domain.post.responses.PostIndexRes;
import com.msa4meerkatgram.domain.post.services.PostService;
import com.msa4meerkatgram.global.responses.GlobalRes;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<GlobalRes<PostIndexRes>> index(PostIndexReq postIndexReq) {
        PostIndexRes postIndexRes = postService.index(postIndexReq);
        return ResponseEntity.status(200).body(
            GlobalRes.<PostIndexRes>builder()
                .code("00")
                .messsage("정상처리")
                .data(postIndexRes)
                .build()
        );
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<GlobalRes<Post>> show(
        @Min(value = 1, message = "1이상 숫자만 허용합니다") @PathVariable long id
    ) {
        Post result = postService.show(id);
        return ResponseEntity.status(200).body(
                GlobalRes.<Post>builder()
                        .code("00")
                        .messsage("게시글 상세 정상 처리")
                        .data(result)
                        .build()
        );
    }

    @PostMapping("/posts")
    public ResponseEntity<GlobalRes<Post>> upload(@RequestBody PostUploadReq postUploadReq, @AuthenticationPrincipal Claims claims) {
        Post result = postService.upload(Long.parseLong(claims.getSubject()), postUploadReq);
        return ResponseEntity.status(200).body(
                GlobalRes.<Post>builder()
                        .code("00")
                        .messsage("게시물 업로드")
                        .data(result)
                        .build()
        );
    }
}

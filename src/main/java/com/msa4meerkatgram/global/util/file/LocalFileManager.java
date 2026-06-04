package com.msa4meerkatgram.global.util.file;

import com.msa4meerkatgram.global.errors.custom.FileManagedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocalFileManager {
    private final FileConfig fileConfig;

    /**
     * 파일 확장자 추출
     * @param file 파일
     * @return 확장자(소문자)
     */
    public String extractExtension(MultipartFile file) {
        // 파일 존재 체크
        if(file == null || file.isEmpty()) {
            throw new FileManagedException("파일 저장 실패: 파일 확장 획득 실패(파일 없음)");
        }

        // 파일 확장자 검증
        String fileName = file.getOriginalFilename();
        if(fileName == null || !fileName.contains(".")) {
            throw new FileManagedException("파일 저장 실패: 파일 확장자 획득 실패(파일명 이상)");
        }
        String extractedExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if(!fileConfig.allowedExtensionList().contains("image/" + extractedExtension)) {
            throw new FileManagedException("파일 저장 실패: 허용하지 않는 파일 확장자");
        }
        
        return extractedExtension;
    }

    /**
     * 랜덤 파일명 생성
     * @return 파일명 'yyyyMMdd_UUID'
     */
    public String generateFileName() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();

        return now.format(dateTimeFormatter) + "_" + UUID.randomUUID();
    }

    public String generateProfilePath(MultipartFile file) {
        return fileConfig.profilePath() + "/" +generateFileName() + "." + extractExtension(file);
    }

    public String generatePostPath(MultipartFile file) {
        return fileConfig.postPath() + "/" + generateFileName() + "." + extractExtension(file);
    }

    // 파일 저장할 디렉토리 생성
    public boolean makeDir(Path targetPath) {
        try {
            if(!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }
            return true;
        } catch (IOException | IllegalStateException e) {
            return false;
        }
    }

    // 업로드된 파일을 서버 디스크에 저장하는 것
    public void saveFile(MultipartFile file, String logicalPath) {
        try {
            // 실제 물리적인 절대 경로 합성 (OS 구분자 자동 보정)
            Path physicalPath = Paths.get(fileConfig.storagePath() + "/" + logicalPath).normalize();
            // 디렉토리 확인 -> physicalPath.getParent() = 여기선 파일명 제외한 폴더 경로
            if(!this.makeDir(physicalPath.getParent())) {
                throw new FileManagedException(String.format("파일 저장 실패: 디렉토리 생성 실패 (경로: %s)", physicalPath.getParent()));
            }
            // 파일 실제 저장
            file.transferTo(physicalPath.toFile());
        } catch (IOException | IllegalStateException e) {
            throw new FileManagedException(String.format("파일 저장 실패: 쓰기 작업 실패 (파일명: %s)", logicalPath));
        }
    }
}

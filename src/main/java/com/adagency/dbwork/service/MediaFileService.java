package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.MediaFileRepository;
import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.mediafile.MediaFileView;
import com.adagency.model.entity.Company;
import com.adagency.model.entity.MediaFile;
import com.adagency.model.mapper.mediaFile.MediaFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class MediaFileService {
	private final MediaFileRepository mediaFileRepository;
	private final MediaFileMapper mediaFileMapper;
	
	@Autowired
	public MediaFileService(MediaFileRepository mediaFileRepository, MediaFileMapper mediaFileMapper){
		this.mediaFileRepository = mediaFileRepository;
		this.mediaFileMapper = mediaFileMapper;
	}
	

	@Transactional
	public MediaFile testCreateWithTransferFileToPathServer(MediaFileCreate mediaFileCreate, String type,
	                            String description, String path, String alt) throws IOException, Exception {
		
		String originalFilename = mediaFileCreate.getFile().getOriginalFilename();
		if (originalFilename == null || originalFilename.isEmpty()) {
			throw new Exception("OriginalFilenameIsEmptyOrNull");
		}
		
		int dotIndex = originalFilename.lastIndexOf('.');
		if (dotIndex == -1 || dotIndex == 0 || dotIndex == originalFilename.length() - 1) {
			throw new Exception("FileDoesNotHaveProperExtensionOrName");
		}
		
		String fileName = originalFilename.substring(0, dotIndex);
		String fileExtension = originalFilename.substring(dotIndex + 1);
		
		if (fileName.isEmpty() || fileExtension.isEmpty()) {
			throw new Exception("FileNameOrExtensionIsEmpty");
		}
		MediaFile mediaFile = mediaFileRepository.save(
				mediaFileMapper.createFromMediaFileCreatetoMediaFile(mediaFileCreate,
				type, description, path, alt)
		);
		saveFile(mediaFileCreate.getFile(), path);
		return mediaFile;
	}
	
	private void saveFile(MultipartFile file, String path) throws IOException {
		File destination = new File(path);
		if (!destination.getParentFile().exists()) {
			destination.getParentFile().mkdirs();
		}
		try {
			file.transferTo(destination);
		} catch (IOException e) {
			if (destination.exists()) {
				destination.delete();
			}
			throw e;
		}
	}
	
	public Optional<MediaFile> getById(Long id){
		return mediaFileRepository.findById(id);
	}
	
	@Transactional
	public void delete(MediaFile mediaFile) throws IOException {
		mediaFileRepository.delete(mediaFile);
		if(mediaFile == null){
			throw new NullPointerException("MediaFileIsNull");
		}
		remove(new File(mediaFile.getPath()), true);
	}
	
	private void remove(File file, Boolean prevDirectory) throws IOException {
		if(file.exists()){
			file.delete();
		}
		if(prevDirectory){
			File directory = file.getParentFile();
			if(directory.isDirectory() && directory.list().length == 0){
				directory.delete();
			}
		}
	}
	
	@Transactional
	public MediaFile update(Long id, String description, String alt, MultipartFile file)
			throws IOException, EntityNotFoundException {
		Optional<MediaFile> mediaFile = mediaFileRepository.findById(id);
		if(!mediaFile.isPresent()){
			throw new EntityNotFoundException("MediaFileWithId=" + id + "NotFound" );
		}
		if(file != null && !file.isEmpty()){
			File rFile  = new File(mediaFile.get().getPath());
			String oldFileName = rFile.getName();
			remove(rFile, false);
			
			String regex = "(.*)" + Pattern.quote(oldFileName) + "$";
			
			String newFilePath = mediaFile.get().getPath().replaceFirst(regex, "$1" + file.getOriginalFilename());
			saveFile(file, newFilePath);
			
			mediaFile.get().setPath(newFilePath);
		}
		if(!description.isEmpty()){
			mediaFile.get().setDescription(description);
		}
		if(!alt.isEmpty()){
			mediaFile.get().setAlt(alt);
		}
		mediaFile.get().setUpdatedAt(new Date());
		return mediaFileRepository.save(mediaFile.get());
	}
	
	
	@Transactional
	public MediaFile updateMedFileV(MediaFileView mediaFileView)
			throws IOException, EntityNotFoundException {
		Optional<MediaFile> mediaFile = mediaFileRepository.findById(mediaFileView.getFileId());
		if(!mediaFile.isPresent()){
			throw new EntityNotFoundException("MediaFileNotFound" );
		}
		if(mediaFileView.getFile() != null && !mediaFileView.getFile().isEmpty()){
			File rFile  = new File(mediaFile.get().getPath());
			String oldFileName = rFile.getName();
			remove(rFile, false);
			
			String regex = "(.*)" + Pattern.quote(oldFileName) + "$";
			
			String newFilePath = mediaFile.get().getPath().replaceFirst(regex, "$1" + mediaFileView.getFile().getOriginalFilename());
			saveFile(mediaFileView.getFile(), newFilePath);
			
			mediaFile.get().setPath(newFilePath);
			mediaFile.get().setSize(mediaFileView.getFile().getSize());
		}
		
		mediaFileMapper.updateMediaFileFromMediaFileView(mediaFile.get(), mediaFileView);
		if(mediaFile.get().getServices() != null && !mediaFile.get().getServices().isEmpty() && mediaFileView.isDeleteFlag()){
			File rFile  = new File(mediaFile.get().getPath());
			remove(rFile,false);
		}
		return mediaFileRepository.save(mediaFile.get());
	}
	


	public String toRelativePath(String absolutePath) {
		return absolutePath.replaceFirst(".*?/ADAgency2/", "");
	}



	public MediaFileView getMediaFileView(MediaFile file){
		MediaFileView mediaFileView = mediaFileMapper.createMediaFileViewFromMediaFile(file);
		mediaFileView.setFilePath(toRelativePath(file.getPath()));
		return mediaFileView;
	}
	
	@Transactional
	public MediaFile save(MediaFile mediaFile){
		return mediaFileRepository.save(mediaFile);
	}
	
}

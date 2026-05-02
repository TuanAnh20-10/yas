package com.yas.media.mapper;

import com.yas.commonlibrary.mapper.BaseMapper;
import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaVmMapper extends BaseMapper<Media, MediaVm> {

	@Override
	@Mapping(target = "url", source = "filePath")
	MediaVm toVm(Media media);

	@Override
	@Mapping(target = "filePath", source = "url")
	Media toModel(MediaVm vm);
}

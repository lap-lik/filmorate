package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.utils.ValidatorUtils;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaDao mpaDao;
    private final MpaMapper mapper;

    @Override
    public MpaDTO create(MpaDTO mpaDTO) {

        ValidatorUtils.validate(mpaDTO, Marker.OnCreate.class);

        return mapper.toDTO(mpaDao.save(mapper.toEntity(mpaDTO)));
    }

    @Override
    public MpaDTO getById(Long mpaId) {

        return mapper.toDTO(mpaDao.findById(mpaId).orElseThrow(() -> NotFoundException.builder()
                .message(String.format("The MPA rating with the ID - `%d` was not found.", mpaId))
                .httpStatus(NOT_FOUND)
                .build()));
    }

    @Override
    public List<MpaDTO> getAll() {

        return mapper.toDTOs(mpaDao.findAll());
    }

    @Override
    public MpaDTO update(MpaDTO mpaDTO) {

        ValidatorUtils.validate(mpaDTO, Marker.OnUpdate.class);

        return mapper.toDTO(mpaDao.update(mapper.toEntity(mpaDTO)).orElseThrow(() -> NotFoundException.builder()
                .message(String.format("The MPA rating `%s` was not found.", mpaDTO.getName()))
                .httpStatus(NOT_FOUND)
                .build()));
    }

    @Override
    public void deleteById(Long mpaId) {

        boolean isMpaDeleted = mpaDao.deleteById(mpaId);

        if (!isMpaDeleted) {
            throw NotFoundException.builder()
                    .message(String.format("The MPA rating with the ID - `%d` was not found.", mpaId))
                    .httpStatus(NOT_FOUND)
                    .build();
        }
    }
}

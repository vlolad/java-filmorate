package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.RatingsDao;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingsService {

    private final RatingsDao ratingsDao;

    public List<MPA> getAll() {
        return ratingsDao.getAllMpa();
    }

    public MPA getMpaById(Integer id) {
        return ratingsDao.getMpaById(id);
    }
}

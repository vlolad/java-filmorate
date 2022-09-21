package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.RatingsDao;

import java.util.List;

@Slf4j
@Service
public class RatingsService {

    private final RatingsDao ratingsDao;

    @Autowired
    public RatingsService(RatingsDao ratingsDao) {
        this.ratingsDao = ratingsDao;
    }

    public List<MPA> getAll() {
        return ratingsDao.getAllMpa();
    }

    public MPA getMpaById(Integer id) {
        return ratingsDao.getMpaById(id);
    }
}

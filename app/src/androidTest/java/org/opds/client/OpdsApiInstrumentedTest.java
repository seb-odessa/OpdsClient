package org.opds.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opds.api.jni.Wrapper;
import org.opds.api.models.Author;
import org.opds.api.models.Book;
import org.opds.api.models.Serie;
import org.opds.api.models.Value;
import org.opds.utils.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class OpdsApiInstrumentedTest {
    private static final String DB_FILE_NAME = "books.db";

    Context appContext;
    private final Wrapper wrapper = new Wrapper();
    private Wrapper.OpdsApi api;

    private <T> List<String> toListOfStrings(Wrapper.Result<List<T>> result) {
        assertTrue(result.isSuccess());
        return result.getValue().stream()
                .map(T::toString)
                .collect(Collectors.toList());
    }

    @Before
    public void setUp() throws IOException {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String dbPath = FileUtils.copyAssetToInternalStorage(appContext, "books.db");
        String uri = String.format("file:%s?mode=ro", dbPath);
        api = wrapper.create(uri);
    }

    @After
    public void tearDown() {
        api.close();
    }

    @Test
    public void packageName() {
        assertEquals("org.opds.client", appContext.getPackageName());
    }

    @Test
    public void isReadonly() {
        assertTrue(api.isReadonly());
    }

    @Test
    public void getAuthorsNextCharByPrefix() {
        Wrapper.Result<List<String>> result = api.getAuthorsNextCharByPrefix("");
        assertTrue(result.isSuccess());
        assertEquals(result.getValue(), Arrays.asList(
                "А Б В Г Д Е Ё Ж З И Й К Л М Н О П Р С Т У Ф Х Ц Ч Ш Щ Э Ю Я д н ф D F K R S W"
                        .split("\\s+")));
    }

    @Test
    public void getSeriesNextCharByPrefix() {
        Wrapper.Result<List<String>> result = api.getSeriesNextCharByPrefix("");
        assertTrue(result.isSuccess());
        assertEquals(result.getValue(), Arrays.asList(
                "А Б В Г Д З И К Л М Н О П Р С Т У Х Ц Ч Ш Э Ю Я п C E L N P 8"
                        .split("\\s+")));
    }

    @Test
    public void getAuthorsByLastName() {
        Wrapper.Result<List<Author>> result = api.getAuthorsByLastName("Кейн");
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of("Адель Кейн", "Рэйчел Кейн"));
    }

    @Test
    public void getSeriesBySerieName() {
        Wrapper.Result<List<Serie>> result = api.getSeriesBySerieName("Кровь на воздух");
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "Кровь на воздух [Павел Сергеевич Иевлев] (2)"));
    }

    @Test
    public void getSeriesByGenreId() {
        Wrapper.Result<List<Serie>> result = api.getSeriesByGenreId(24);
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "Варяг [Мазин] [Александр Владимирович Мазин] (1)",
                "Восток (РИПОЛ) [Владимир Вячеславович Малявин] (1)"));
    }

    @Test
    public void getAuthorsByGenreId() {
        Wrapper.Result<List<Author>> result = api.getAuthorsByGenreId(24);
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of("Дмитрий Михайлович Балашов",
                "Анатолий Сергеевич Бернацкий",
                "Александр Владимирович Волков",
                "Сергей Михайлович Голицын",
                "Сара Гриствуд",
                "Александр Владимирович Мазин",
                "Владимир Вячеславович Малявин",
                "Александр Викторович Марков",
                "Лев Карлосович Масиель Санчес",
                "Говард Пайл",
                "Джеймс Перкинс",
                "Джордж Сартон",
                "Евгений Викторович Старшов",
                "Дон Холлуэй",
                "Петер Шрайнер"));
    }

    @Test
    public void getBooksByGenreIdAndDate() {
        Wrapper.Result<List<Book>> result = api.getBooksByGenreIdAndDate(24, "2024-06-0%");
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "Игра королев. Женщины, которые изменили историю Европы - Сара Гриствуд (2024-06-07) [2.67 MB]",
                "Рыцари, закованные в сталь - Говард Пайл (2024-06-01) [2.46 MB]"));

    }

    @Test
    public void getGenresByMeta() {
        Wrapper.Result<List<Value>> result = api.getGenresByMeta("Деловая литература");
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "Карьера, кадры",
                "Маркетинг, PR",
                "Финансы",
                "Экономика"));
    }

    @Test
    public void getMetaGenres() {
        Wrapper.Result<List<Value>> result = api.getMetaGenres();
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "Деловая литература",
                "Детективы и Триллеры",
                "Документальная литература",
                "Дом и семья",
                "Драматургия",
                "Искусство, Искусствоведение, Дизайн",
                "Компьютеры и Интернет",
                "Литература для детей",
                "Любовные романы",
                "Наука, Образование",
                "Поэзия",
                "Приключения",
                "Проза",
                "Прочее",
                "Религия, духовность, Эзотерика",
                "Справочная литература",
                "Старинное",
                "Техника",
                "Учебники и пособия",
                "Фантастика",
                "Фольклор",
                "Эзотерика",
                "Юмор"));
    }

    @Test
    public void getAuthorsByBooksIds() {
        Wrapper.Result<List<Author>> result = api.getAuthorsByBooksIds(new int[] { 768409, 768571, 768746, 768750 });
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "Анатолий Сергеевич Бернацкий",
                "Сара Гриствуд",
                "Александр Викторович Марков",
                "Говард Пайл"));
    }

    @Test
    public void getSeriesByAuthorIds() {
        Wrapper.Result<List<Serie>> result = api.getSeriesByAuthorIds(50, 42, 281);
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "Кровь на воздух [Павел Сергеевич Иевлев] (2)"));
    }

    @Test
    public void getAuthorByIds() {
        Wrapper.Result<Author> result = api.getAuthorByIds(50, 42, 281);
        assertTrue(result.isSuccess());
        assertEquals(result.getValue().toString(), "Павел Сергеевич Иевлев");
    }

    @Test
    public void getBooksByAuthorIds() {
        Wrapper.Result<List<Book>> result = api.getBooksByAuthorIds(43, 2, 184);
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "День писателя - Анна Велес (2024-06-18) [976.19 KB]",
                "2 Хозяин мрачного замка - Анна Велес (2024-06-05) [1.91 MB]"));
    }

    @Test
    public void getBooksByAuthorIdsWithoutSerie() {
        Wrapper.Result<List<Book>> result = api.getBooksByAuthorIdsWithoutSerie(43, 2, 184);
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "День писателя - Анна Велес (2024-06-18) [976.19 KB]"));
    }

    @Test
    public void getBooksByAuthorIdsAndSerieId() {
        Wrapper.Result<List<Book>> result = api.getBooksByAuthorIdsAndSerieId(43, 2, 184, 29);
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "2 Хозяин мрачного замка - Анна Велес (2024-06-05) [1.91 MB]"));
    }

    @Test
    public void getBooksBySerieId() {
        Wrapper.Result<List<Book>> result = api.getBooksBySerieId(29);
        assertTrue(result.isSuccess());
        assertEquals(toListOfStrings(result), List.of(
                "2 Хозяин мрачного замка - Анна Велес (2024-06-05) [1.91 MB]"));
    }
}
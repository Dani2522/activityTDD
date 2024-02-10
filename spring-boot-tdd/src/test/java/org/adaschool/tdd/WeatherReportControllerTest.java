package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.MongoWeatherService;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Optional;

import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Cond.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherReportControllerTest {

    @Mock
    WeatherReportRepository repository;

    WeatherService weatherService;

    @BeforeEach
    void setup() {
        weatherService = new MongoWeatherService(repository);
    }

    @Test
    void createWeatherReportCallsSaveOnRepository() {
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation(lat, lng);
        WeatherReportDto weatherReportDto = new WeatherReportDto(location, 35f, 22f, "tester", new Date());
        WeatherReport weatherReport = new WeatherReport(
                weatherReportDto.getGeoLocation(),
                weatherReportDto.getTemperature(),
                weatherReportDto.getHumidity(),
                weatherReportDto.getReporter(),
                new Date());


        when(repository.save(any(WeatherReport.class))).thenReturn(weatherReport);


        weatherService.report(weatherReportDto);


        verify(repository, times(1)).save(any(WeatherReport.class));
    }

    @Test
    void weatherReportIdFoundTest() {
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation(lat, lng);
        WeatherReport weatherReport = new WeatherReport(location, 35f, 22f, "tester", new Date());
        when(repository.findById(weatherReportId)).thenReturn(Optional.of(weatherReport));
        WeatherReport foundWeatherReport = weatherService.findById(weatherReportId);
        Assertions.assertEquals(weatherReport, foundWeatherReport);
    }

    @Test
    void weatherReportIdNotFoundTest() {
        String weatherReportId = "dsawe1fasdasdoooq123";
        when(repository.findById(weatherReportId)).thenReturn(Optional.empty());
        Assertions.assertThrows(WeatherReportNotFoundException.class, () -> {
            weatherService.findById(weatherReportId);
        });
    }
}
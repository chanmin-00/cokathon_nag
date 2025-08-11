package com.example.bojmate.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelativeTimeFormatter {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // 년-월-일 시:분

    // 상대 시간 형식으로 변환하는 메서드
    public static String formatRelativeTime(LocalDateTime createdDate) {
        LocalDateTime now = LocalDateTime.now(); // 현재 시간
        Duration duration = Duration.between(createdDate, now);

        // 초 단위 계산
        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + "초 전";
        }

        // 분 단위 계산
        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + "분 전";
        }

        // 시간 단위 계산
        long hours = duration.toHours();
        if (hours < 24) {
            return hours + "시간 전";
        }

        // 일 단위 계산
        long days = duration.toDays();
        if (days < 7) {
            return days + "일 전";
        }

        // 주 단위 계산
        long weeks = days / 7;
        if (weeks < 4) {
            return weeks + "주 전";
        }

        return createdDate.format(FORMATTER);
    }

    // 상대 시간 문자열을 파싱하여 LocalDateTime 객체로 변환하는 메서드
    public static LocalDateTime parseRelativeTime(String relativeTime) {
        LocalDateTime now = LocalDateTime.now();

        Pattern pattern = Pattern.compile("(\\d+)\\s*(분|시간|일) 전");
        Matcher matcher = pattern.matcher(relativeTime.trim());

        if (matcher.find()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            return switch (unit) {
                case "분" -> now.minusMinutes(amount);
                case "시간" -> now.minusHours(amount);
                case "일" -> now.minusDays(amount);
                default -> now;
            };
        }

        return now; // fallback: 현재 시각 반환
    }
}


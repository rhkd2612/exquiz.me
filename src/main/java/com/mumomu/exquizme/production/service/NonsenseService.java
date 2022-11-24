package com.mumomu.exquizme.production.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NonsenseService {
    List<Pair<String, String>> list = Arrays.asList(
            Pair.of("하늘에서 애 낳으면?", "하이애나"),
            Pair.of("정말 멋진 신사가 자기 소개하는 것은?", "신사임당"),
            Pair.of("세상에서 가장 먼저 자는 가수는?", "이미자"),
            Pair.of("속이 끓어오르는 사람이 쓴 글은?", "부글부글"),
            Pair.of("아침 저녁으로 발만 동동 구르고 못 얻어 먹는 것은?", "젓가락"),
            Pair.of("길이가 2Km나 되는 발은?", "오리발"),
            Pair.of("먹으면 죽는데 안 먹을 수 없는 것은?", "나이"),
            Pair.of("미소의 반대말은?", "당기소"),
            Pair.of("언제나 잘못을 비는 나무는?", "사과나무"),
            Pair.of("세상에서 제일 뜨거운 과일은?", "천도복숭아"),
            Pair.of("실패하면 살고 성공하면 죽는 것은?", "자살"),
            Pair.of("프로 야구단이 하나씩 가지고 있는 거지는?", "본거지"),
            Pair.of("2 더하기 2는?", "덧니"),
            Pair.of("가만히 있어도 못 잡는 것은?", "그림자"),
            Pair.of("가장 싼 사냥 도구는?", "파리채"),
            Pair.of("가죽 먼저 벗기고 털을 뜯는 것은?", "옥수수"),
            Pair.of("개가 사람을 가르친다를 네 글자로 줄이면?", "개인지도"),
            Pair.of("개와 사람이 동업을 하면?", "개인사업"),
            Pair.of("건강한 사람이 피로해서 좋은 일은?", "헌혈"),
            Pair.of("검정 줄무늬가 있는 초록 집의 빨간 방에서 검은 형제들이 모여 사는 것은?", "수박"),
            Pair.of("깎으면 깎을수록 커지는 것은?", "구멍"),
            Pair.of("깎으면 깎을수록 길어지는 것은?", "연필심"),
            Pair.of("깜깜한 곳에서만 아름답게 보이는 꽃은?", "불꽃"),
            Pair.of("남이 이상해야 먹고 사는 사람은?", "치과의사"),
            Pair.of("노총각과 노처녀가 선을 보려고 타러 가는 버스는?", "노선버스"),
            Pair.of("농촌에서 봄마다 돈 받고 하는 내기는?", "모내기"),
            Pair.of("누구나 발 벗고 나서야 할 수 있는 일은?", "발씻는일"),
            Pair.of("눈 깜짝할 사이에 할 수 있는 일은?", "윙크"),
            Pair.of("눈을 봐야 먹고 사는 사람은?", "안과의사"),
            Pair.of("다섯은 당기고 다섯은 들어가는 것은?", "장갑"),
            Pair.of("닦으면 닦을수록 더러워지는 것은?", "걸레"),
            Pair.of("매일 매일 망쳐야 먹고 사는 사람은?", "어부"),
            Pair.of("매일같이 가서 두드려도 들어오라는 소리를 못 들어보는 곳은?", "화장실"),
            Pair.of("못사는 사람이 많아야 잘 되는 가게는?", "철물점"),
            Pair.of("병든 자여, 내게로 오라고 외치는 사람은?", "고물장수"),
            Pair.of("보통의 반대는?", "곱배기"),
            Pair.of("불을 일으키는 비는?", "성냥개비"),
            Pair.of("빨간 옷을 입고 길가에 서서 종이를 받아먹는 것은?", "우체통"),
            Pair.of("사각형이 분명한데 원이라고 하는 것은?", "지폐"),
            Pair.of("사람답게 살고 싶을 때 내는 표는?", "사표"),
            Pair.of("손님이 뜸하면 돈 버는 사람은?", "한의사"),
            Pair.of("앉으면 멀어지고 일어서면 가까워지는 것은?", "천장"),
            Pair.of("앞을 가려야만 잘 보이는 것은?", "안경"),
            Pair.of("얼굴은 여섯이고 눈은 21인 것은?", "주사위"),
            Pair.of("아무리 만원버스를 타도 앉아서 가는 사람은?", "운전사"),
            Pair.of("자꾸 맞아야 사는 것은?", "팽이")
    );

    public Pair<String, String> getNonsense() {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

}

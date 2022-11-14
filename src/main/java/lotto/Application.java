package lotto;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.Randoms;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Application {

    public static int LOTTO_PRICE = 1000;

    public static void main(String[] args) {
        // TODO: 프로그램 구현



        System.out.println("구입 금액을 입력 해주세요.");
        // 구입 금액 입력 받기
        int account = setAccount();
        System.out.println("account = " + account);

        // 로또 생성
        List<List<Integer>> lottos = buyLotto(account);

        // 로또 출력
        System.out.println(lottos.size() + "개를 구매했습니다.");
        lottos.stream().forEach(System.out::println);

        // 당첨 번호 , 보너스 번호 입력 받기
        Set<Integer> winnerNumber = setWinnerNumber();
        System.out.println("winnerNumber = " + winnerNumber);

        int bonusNumber = setBonusNumber(winnerNumber);
        System.out.println("bonusNumber = " + bonusNumber);

        System.out.println("보너스 넘버 완료 ================");

        // 로또 당첨 여부 확인
        List<List<Integer>> result = checkLotto(lottos, winnerNumber, bonusNumber);
        System.out.println("result = " + result);

        // 로또 당첨 내역 통계
        List<int[]> sumResult = calResult(result);

        // 로또 당첨 내역 출력
        announceResult(sumResult);

        // 수익율 계산
        calYield(sumResult , account);


    }

    private static void calYield(List<int[]> sumResult , int account) {
        int total = 0;
        int[] rightNumber = sumResult.get(0);
        int[] bonusNumber = sumResult.get(1);
        int[] value = {0, 0, 0, 5000, 50000, 1500000, 30000000 , 2000000000};
        for (int i = 3; i <= 6; i++) {
            if(rightNumber[i] > 0){
                total += (rightNumber[i] * value[i]);
            }
            if (rightNumber[5] >= 1 && bonusNumber[i] > 0) {
                total += (rightNumber[i] * value[6]);
            }
            if (rightNumber[6] >= 1){
                total += (rightNumber[i] * value[7]);
            }
        }

        System.out.println("총 수익 : " + total);
        System.out.println("총 투자 금액 : " + account);
        float yield =  (account / (float) total) * 100;
        System.out.println("총 수익률은" + yield + "% 입니다.");
    }

    private static void announceResult(List<int[]> sumResult) {
        int[] rightNumber = sumResult.get(0);
        int[] bonusNumber = sumResult.get(1);
        for (int i = 3; i <= 6; i++) {
            if(rightNumber[i] > 0){
                System.out.println(i+"개 일치 (5,000원) - "+ rightNumber[i] + "개");
            }
            if (bonusNumber[i] > 0) {
                System.out.println(i+"개 일치, 보너스 볼 일치 (5,000원) - "+ bonusNumber[i] + "개");
            }
        }
    }

    private static List<int[]> calResult(List<List<Integer>> result) {
        int[] rightResult = {0, 0, 0, 0, 0, 0, 0};
        int[] rightBonusResult = {0, 0, 0, 0, 0, 0, 0};
        result.forEach(lotto -> {
            int rightNumber = lotto.get(0);
            int bonusNumber = lotto.get(1);
            if(bonusNumber >= 1){
                rightBonusResult[rightNumber]++;
            }
            if (bonusNumber == 0) {
                rightResult[rightNumber]++;
            }
        });
        return List.of(rightResult, rightBonusResult);
    }


    private static List<List<Integer>> checkLotto(List<List<Integer>> lottos, Set<Integer> winnerNumber, int bonusNumber) {
        List<List<Integer>> resultLotto = new ArrayList<>();
        lottos.forEach(lotto -> {
            resultLotto.add(checkNumber(lotto, winnerNumber, bonusNumber));
        });
        return resultLotto;
    }

    private static List<Integer> checkNumber(List<Integer> lotto, Set<Integer> winnerNumber, int bonusNumber) {
        AtomicInteger rightNumbers = new AtomicInteger();
        AtomicInteger rightBonusNumber = new AtomicInteger();
        lotto.forEach(number -> {
            if(winnerNumber.contains(number)){
                rightNumbers.getAndIncrement();
            }
            if (bonusNumber == number) {
                rightBonusNumber.getAndIncrement();
            }
        });
      return List.of(rightNumbers.intValue(), rightBonusNumber.intValue());
    }

    private static int setBonusNumber(Set<Integer> winnerNumber) {
        String input = Console.readLine();
        int bonusNumber = 0;
        if(validBonusNumber(input)){
            bonusNumber =  Integer.parseInt(input);
        }
        if(winnerNumber.contains(bonusNumber)){
            throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_VALUE.message());
        }
        if(bonusNumber >= 1 && bonusNumber <= 45){
            return bonusNumber;
        }
        throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_VALUE.message());
    }

    private static boolean validBonusNumber(String input) {
        if(Pattern.matches("\\d?\\d", input)){
            return true;
        }
        throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_VALUE.message());
    }


    private static Set<Integer> setWinnerNumber() {
        String input = Console.readLine();
        Set<Integer> winnerNumber = new HashSet<>();
        // 입력 받은 당첨 번호 검증
        if(validWinnerNumber(input)){
            winnerNumber = Stream.of(input.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
        }
        if(winnerNumber.size() == 6){
            return winnerNumber;
        }
        throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_VALUE.message());
    }

    private static boolean validWinnerNumber(String input) {
        // 정규표현식 1 ~ 45 의 수 확인하기 -> 숫자 검증 로직을 별도 생성
        validRegex(input);
        // 입력 받은 당첨 번호의 값이 1~45 사이인지 검증
        validNumberRange(input);
        return true;
    }

    private static void validNumberRange(String input) {
        Stream.of(input.split(",")).map(Integer::parseInt).forEach(x-> {
            if (x < 1 || x > 45) {
                throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_VALUE.message());
            }
        });
    }

    private static boolean validRegex(String input) {
        if(Pattern.matches("((\\d?\\d,){5}\\d?\\d)", input)){
            return true;
        }
        throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_VALUE.message());
    }

    private static List<List<Integer>> buyLotto(int account) {
        List<List<Integer>> lottos = new ArrayList<>();
        while (account > 0) {
            List<Integer> list = Randoms.pickUniqueNumbersInRange(1, 45, 6);
            Collections.sort(list);
            lottos.add(list);
            account -= LOTTO_PRICE;
        }
        return lottos;
    }

    private static int setAccount() {
        int account = Integer.parseInt(Console.readLine());
        // 입력 받은 금액 검증
        if(validAccount(account)){
            throw new IllegalArgumentException(ExceptionMessage.INVALID_INPUT_VALUE.message());
        }
        return account;
    }

    private static boolean validAccount(int account) {
        if (account % 1000 == 0) {
            return false;
        }
        return true;
    }


}

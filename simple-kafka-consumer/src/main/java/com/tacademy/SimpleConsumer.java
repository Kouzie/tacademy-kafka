package com.tacademy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class SimpleConsumer {
    private static String TOPIC_NAME = "test";
    private static String GROUP_ID = "testgroup";
    private static String BOOTSTRAP_SERVERS = "3.23.88.6:9092";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);

        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true) {
            // 1초간 기다렸다 등록된 모든 레코드를 읽어와서 처리한다.
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            // poll 메서드 호출시 commit 할 시간이 되었는지 확인하고, 해당 시간이 넘었다면 커밋한다.
            // 기본적으로 auto commit 이 설정되어있음으로 매우 편리, 기본값은 5초이다.
            // configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); <- 기본
            // 단점은 컨슈머 강제 중단시 commit 이 되지않으면서 다음번에 실행될때 다시 가져오면서 중복처리된다.
            // 중복처리를 방지하려면 auto commit 을 사용하지 않고 특정 로직 수행마다 수동 커밋, 에러발생시에도 커밋하면 된다.
            // 파티션당 컨슈머 한개이기에 여러개의 컨슈머가 하나의 파티션을 두고 경합할 일은 없음으로 dirty read 걱정은 하지 않아도 된다.
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value());
            }
        }
    }
}
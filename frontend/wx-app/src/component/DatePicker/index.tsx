import React, { useEffect, useState } from "react";
import { Swiper, SwiperItem } from "@antmjs/vantui";
import { Text, View } from "@tarojs/components";
import dayjs, { Dayjs } from "dayjs";
import styles from "./index.module.less";
import classNames from "classnames";

// 获取给定日期的周一日期
const getStartOfWeek = (date: dayjs.Dayjs) => {
  return date.startOf("week"); // 获取给定日期所在周的周一
};

// 获取从某个周一开始的日期（7天）
const getWeekDates = (startOfWeek: dayjs.Dayjs) => {
  const weekDates: Dayjs[] = [];
  for (let i = 0; i < 7; i++) {
    weekDates.push(startOfWeek.add(i, "day")); // 获取当前周的所有日期
  }
  return weekDates;
};
// 获取本周、下一周、下两周、上一周、上两周的日期（每周的周一到周日）
const getWeeksAround = (current: dayjs.Dayjs) => {
  const weeks: Dayjs[][] = [];
  const range = new Array(21).fill("").map((_it, idx) => idx - 7); // 上两周、上一周、本周、下一周、下两周
  range.forEach((weekOffset) => {
    const startItem = current.add(weekOffset, "week");
    const startOfWeek = getStartOfWeek(startItem);
    weeks.push(getWeekDates(startOfWeek));
  });

  return weeks;
};

const SwiperDatePicker: React.FC<{
  value?: Dayjs;
  onChange: (value: Dayjs) => void;
}> = ({ value: valueProp, onChange }) => {
  const [value, setValue] = useState<Dayjs | undefined>();

  const [currentIndex, setCurrentIndex] = useState(1);

  const [weekDates, setWeekDates] = useState<any[]>([]);

  useEffect(() => {
    getWeeksAround(value || dayjs());
    setValue(valueProp || dayjs().subtract(1, "week"));

    if (!weekDates?.length) {
      setWeekDates(getWeeksAround(valueProp || dayjs()));
    }
  }, [valueProp]);

  const curMon = weekDates?.[currentIndex]?.[3]?.format("MM");
  return (
    <View className={styles["yoo-swiper-date-picker"]}>
      <View className={styles["yoo-date-month"]}>{curMon}月</View>
      {/* 日期选择器 */}
      <Swiper
        initPage={currentIndex}
        height={44}
        onChange={(e) => {
          setCurrentIndex(e);
        }}
        loop={false}
        autoPlay={0}
      >
        {weekDates.map((date, index) => (
          <SwiperItem key={index}>
            <View className={styles["yoo-date-week"]}>
              {date.map((day, i) => (
                <View
                  key={i}
                  onClick={() => {
                    onChange?.(day);
                  }}
                  className={classNames(
                    styles["yoo-date-item"],
                    day.format("YYYY-MM-DD") === value?.format("YYYY-MM-DD")
                      ? styles["yoo-date-item-active"]
                      : null
                  )}
                >
                  <View className={styles["yoo-date-item-week"]}>
                    <Text className={styles["yoo-date-item-text"]}>
                      {["日", "一", "二", "三", "四", "五", "六"][day.day()]}
                    </Text>
                  </View>
                  <View className={styles["yoo-date-item-date"]}>
                    <Text className={styles["yoo-date-item-text"]}>
                      {day.date()}
                    </Text>
                  </View>
                </View>
              ))}
            </View>
          </SwiperItem>
        ))}
      </Swiper>
    </View>
  );
};

export default SwiperDatePicker;

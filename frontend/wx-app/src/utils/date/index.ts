import dayjs from "dayjs";

export const getFormatWeekdays = (timestamp: number) => {
  // 将时间戳转换为 Date 对象
  const date = new Date(timestamp);

  // 获取星期几
  const weekdays = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
  return weekdays[date.getDay()]; // 返回对应的星期几
};

export function formatSortTime(date: number | dayjs.Dayjs) {
  const now = dayjs();
  const target = dayjs(date);

  if (target.isSame(now, "day")) {
    return target.format("HH:mm"); // 今天，显示小时:分钟
  } else if (target.isSame(now, "year")) {
    return target.format("MM/DD HH:mm"); // 今年，显示 月-日
  } else {
    return target.format("YYYY-MM-DD HH:mm"); // 不是今年，显示完整日期
  }
}

export function formatMiddleTime(date: number | dayjs.Dayjs) {
  const now = dayjs();
  const target = dayjs(date);

  if (target.isSame(now, "year")) {
    return target.format("MM-DD HH:mm"); // 今年，显示 月-日
  } else {
    return target.format("YYYY-MM-DD HH:mm"); // 不是今年，显示完整日期
  }
}

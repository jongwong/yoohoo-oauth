export const getFormatWeekdays = (timestamp: number) => {
  // 将时间戳转换为 Date 对象
  const date = new Date(timestamp);

  // 获取星期几
  const weekdays = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
  return weekdays[date.getDay()]; // 返回对应的星期几
};

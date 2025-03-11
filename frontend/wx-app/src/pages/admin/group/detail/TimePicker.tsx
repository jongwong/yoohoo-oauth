import React, { useState } from "react";
import { Input, View } from "@tarojs/components";
import { DatetimePicker, Popup } from "@antmjs/vantui";
import dayjs from "dayjs";

interface TimePickerProps {
  value?: number; // 选中的时间（时间戳）
  onChange?: (e: { detail: { value: number } }) => void; // 选中时间后的回调（返回时间戳）
}

const TimePicker: React.FC<TimePickerProps> = ({ value, onChange }) => {
  const [visible, setVisible] = useState(false);

  const handleConfirm = (val) => {
    onChange?.(val); // 直接返回时间戳
    setVisible(false);
  };

  return (
    <View>
      <Input
        value={value ? dayjs(value).format("YYYY-MM-DD HH:mm:ss") : "请选择"}
        placeholder="点击搜索"
        onClick={() => setVisible(true)}
      />

      <Popup position="bottom" show={visible} onClose={() => setVisible(false)}>
        <DatetimePicker
          type="datetime"
          value={value}
          onConfirm={handleConfirm}
          onCancel={() => setVisible(false)}
        />
      </Popup>
    </View>
  );
};

export default TimePicker;

import { Input, InputProps } from "@tarojs/components";
import React, { useEffect, useState } from "react";

import { isNumber } from "lodash-es";

const InputNumber: React.FC<
  InputProps & {
    onChange?: (value?: number) => void;
    precision?: number;
  }
> = ({ value, precision = 0, onBlur, onChange, ...rest }) => {
  const [innerValue, setInnerValue] = useState<string>();

  useEffect(() => {
    // @ts-ignore
    if (value === Number(innerValue) || value === innerValue) {
      return;
    }
    const val = Number(value);
    setInnerValue(isNumber(val) ? String(val) : "");
  }, [value]);

  const handleInputChange = (e) => {
    setInnerValue(e.detail.value);

    const val = Number(e.detail.value);
    onChange?.(isNumber(val) ? val : undefined);
  };
  return (
    <Input
      {...rest}
      onBlur={(e) => {
        let val = Number(innerValue);
        if (isNumber(val) && precision) {
          val =
            Math.floor(val * Math.pow(10, precision)) / Math.pow(10, precision);
          onChange?.(val);
        }
        onBlur?.(e);
      }}
      type={"digit"}
      className="yo-input-number-field"
      onInput={handleInputChange}
      value={innerValue}
    />
  );
};

export default InputNumber;

import { Image as TaroImage, ImageProps } from "@tarojs/components";
import React, { useState } from "react";
import { getFallbackImageUrl } from "@/utils/file";

const Image: React.FC<
  {
    fallback?: boolean;
    fallbackSrc?: string;
  } & ImageProps
> = ({ src, fallback, fallbackSrc = getFallbackImageUrl(), ...props }) => {
  const [hasError, setHasError] = useState(false);
  const handleError = () => {
    setHasError(true); // 设置为备用图片
  };
  return (
    <TaroImage
      src={hasError && fallback ? fallbackSrc : src}
      onError={handleError}
      {...props}
    />
  );
};

export default Image;

export const generateFileUrl = (
  filePath: string,
  fallback: boolean = false
): string | undefined => {
  const domain = "https://yoohoo-oss.oss-cn-shanghai.aliyuncs.com"; // 这里是你的 OSS 域名
  if (!filePath) {
    return fallback ? getFallbackImageUrl() : "";
  }
  // eslint-disable-next-line no-constant-condition
  return `${domain}${filePath.startsWith("/") ? "" : "/"}${filePath}`;
};

export const getFallbackImageUrl = (): string => {
  return "https://yoohoo-oss.oss-cn-shanghai.aliyuncs.com/miniapp/暂无图片1.png";
};
export const getNoDataUrl = (): string => {
  return "https://yoohoo-oss.oss-cn-shanghai.aliyuncs.com/miniapp/暂无内容.png";
};

export const getNoProductUrl = (): string => {
  return "https://yoohoo-oss.oss-cn-shanghai.aliyuncs.com/miniapp/暂无商品.png";
};

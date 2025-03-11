import React, { useEffect, useMemo, useState } from "react";
import { View } from "@tarojs/components";
import { Uploader } from "@antmjs/vantui";
import Taro from "@tarojs/taro";
import dayjs from "dayjs";
import { serviceConfig } from "@/config";
import { generateFileUrl } from "@/utils/file";
import { cloneDeep } from "lodash-es";

const generateDatePath = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0"); // 补齐两位
  const day = String(now.getDate()).padStart(2, "0"); // 补齐两位

  return `${year}${month}${day}`; // 返回 YYYY/MM/DD 格式
};

// 生成标准 UUID v4
const generateUUID = (): string => {
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
};

// 生成 OSS 文件名
const generateFileName = (filePath: string) => {
  const timeStamp = dayjs().format("HHmmssSSS");
  const uid = Math.floor(Math.random() * 10000);
  const ext = filePath.includes(".") ? filePath.split(".").pop() : "jpg"; // 取扩展名，默认 `jpg`
  return `${generateUUID()}.${ext}`;
};

const OssUpload: React.FC<{
  value?: Array<{ url: string; name: string }>;
  onChange?: (value: Array<{ url: string; name: string }>) => void;
  maxCount?: number;
}> = ({ value = [], onChange, maxCount = 1 }) => {
  const [fileList, setFileList] = useState(value || []);

  useEffect(() => {
    setFileList(cloneDeep(value));
  }, [value]);

  const handleUpload = async (event) => {
    const { file } = event.detail;
    if (!file) return;
    const datePath = generateDatePath();
    const name = file.url.split("/").pop();
    const fileName = `dev/${datePath}/${generateFileName(name)}`;

    const token = wx.getStorageSync("access_token");

    Taro.uploadFile({
      url: `${serviceConfig.client}/client/oss/upload`,
      name: "file",
      filePath: file.url,
      formData: {
        path: fileName,
      },
      header: {
        Authorization: token ? `Bearer ${token}` : undefined, // 携带身份验证
        "Content-Type": "multipart/form-data", // 确保 multipart/form-data
      },
      success: (res) => {
        try {
          const _data = JSON.parse(res.data);

          if (_data?.code === 500) {
            Taro.showToast({
              title: "上传失败",
              icon: "none",
            });
          } else {
            const _name = fileName.split("/").pop();
            const newFileList = [
              ...fileList,
              { url: fileName, name: _name },
            ].slice(-maxCount);
            setFileList(newFileList);
            onChange?.(newFileList);
          }
        } catch (e) {}
      },
    });
  };

  const handleRemove = (event) => {
    const newFileList = fileList.filter(
      (_, index) => index !== event.detail.index
    );

    setFileList(newFileList);
    onChange?.(newFileList);
  };

  const formatFileList = useMemo(() => {
    return fileList?.map((item) => {
      if (item?.url?.startsWith("http")) {
        return item;
      }
      return {
        ...item,
        url: generateFileUrl(item.url),
      };
    });
  }, [fileList]);
  return (
    <View>
      <Uploader
        fileList={formatFileList}
        maxCount={maxCount}
        onAfterRead={handleUpload}
        onDelete={handleRemove}
      />
    </View>
  );
};

export default OssUpload;

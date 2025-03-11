import React from "react";
import type { TagProps as RawTagProps } from "@antmjs/vantui";
import { Tag as RawTag } from "@antmjs/vantui";
import { ValueEnumStatusType } from "@/utils/enum/types";

const statusMap = {
  success: "success",
  error: "danger",
  processing: "primary",
  default: "default",
  warning: "warning",
};
type TagProps = {
  status?: ValueEnumStatusType;
} & Omit<RawTagProps, "type">;
const Tag: React.FC<TagProps> = (props) => {
  const { status, ...rest } = props;
  return <RawTag {...rest} type={statusMap[status] as any} />;
};
export default Tag;

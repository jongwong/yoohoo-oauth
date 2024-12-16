import {ValueEnumType} from "@/utils/enum";
import {Key, ReactNode} from "react";

export type BaseProFieldType<T = any> = {
    valueType?: string; // ProField 类型，如 'text', 'select', 'dateRange'
    valueEnum?: ValueEnumType,
    placeholder?: string; // 输入提示
    fieldProps?: Record<string, any>; // 额外字段属性
    renderFormItem?: (t: any, r: T) => ReactNode
    name?: Key | Key[]; // 字段名
    label?: ReactNode; // 显示的标签
    title?: ReactNode; // 字段名
    dataIndex?: Key | Key[]; // 显示的标签
}

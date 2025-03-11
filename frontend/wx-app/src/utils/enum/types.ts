export type TrimTextField<T extends Record<string, any>> = Omit<T, "text">;

export type ValueEnumColorType =
  | "pink"
  | "red"
  | "yellow"
  | "orange"
  | "cyan"
  | "green"
  | "blue"
  | "purple"
  | "geekblue"
  | "magenta"
  | "volcano"
  | "gold"
  | "lime"
  // eslint-disable-next-line @typescript-eslint/ban-types
  | (string & {});

export type ValueEnumStatusType =
  | "success"
  | "error"
  | "processing"
  | "default"
  | "warning";

/** 基础字段 */
export type BaseValueType<T> = {
  value: T;
  text: string;
};

/** 额外可选字段 */
export type ExtraValueType = {
  color?: ValueEnumColorType;
  status?: ValueEnumStatusType;
};

export type ValueEnumType<
  T = number,
  P extends Record<string, any> = Record<string, any>
> = BaseValueType<T> & ExtraValueType & P;

export type EnumMapListParams<
  U extends "text" | "label" = "text",
  O extends boolean = false
> = {
  limitValue?: any;
  limitText?: string;
  addUnLimit?: boolean;
  isOptions?: O;
  type?: U;
  /** 根据 value 过滤掉指定项 */
  excludeByValues?: any[];
};

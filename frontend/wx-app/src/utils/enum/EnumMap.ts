import { EnumMapListParams, ValueEnumType } from "./types";
import { omit } from "lodash";

const EMPTY_TEXT = "--";
const DEFAULT_TO_VAL_ENUM_LIST_PARAMS: EnumMapListParams = {
  limitValue: -1,
  limitText: "不限",
  addUnLimit: false,
  type: "text",
  isOptions: false,
};

/**
 * 枚举 Map
 * @description toValEnumMap 内部使用
 */
export class EnumMap<T = any, V extends ValueEnumType<T> = ValueEnumType<T>>
  extends Map<V["value"], V>
  implements ReadonlyMap<V["value"], V>
{
  constructor(entries: readonly [V["value"], V][]) {
    super(entries);
    // 剔除修改 Map 的相关方法
    this.set = () => this;
    this.clear = () => false;
    this.delete = () => false;
  }

  /**
   * 根据 value 值获取对应文本
   * @param value
   * @returns
   */
  getText(value?: V["value"], defaultText: string = EMPTY_TEXT) {
    if (value === undefined || value === null) {
      return defaultText;
    }
    return this.get(value)?.text ?? defaultText;
  }

  list(config: EnumMapListParams<"text">): V[];
  list(config: EnumMapListParams<"label">): (V & { label: string })[];
  list(
    config: EnumMapListParams<"text" | "label", true>
  ): (V & { label: string })[];
  list(config?: EnumMapListParams): V[];
  list(config: EnumMapListParams<"text" | "label", boolean> = {}): V[] {
    const finalConfig = {
      ...DEFAULT_TO_VAL_ENUM_LIST_PARAMS,
      ...config,
    };
    const {
      limitText,
      limitValue,
      addUnLimit,
      type,
      isOptions,
      excludeByValues = [],
    } = finalConfig;

    const _list = Array.from(this.values()).filter(
      (item) => !(excludeByValues as any)?.includes(item.value)
    );
    if (addUnLimit) {
      _list.unshift({ value: limitValue, text: limitText } as V);
    }
    if (type === "label" || isOptions) {
      return _list.map((item) => ({
        ...omit(item, ["text"]),
        label: item.text,
      })) as any;
    }

    return _list;
  }

  options(...[config, ...otherParams]: Parameters<typeof this.list>) {
    return this.list({ ...config, isOptions: true }, ...otherParams);
  }

  tabs(...params: Parameters<typeof this.list>) {
    return this.list(...params).map((item) => ({
      ...omit(item, ["text", "value"]),
      key: String(item.value),
      label: item.text,
    }));
  }
}

/** 只读的 EnumMap，从定义上剔除了 set/clear/delete 方法 */
export type ReadonlyEnumMap<
  T = any,
  V extends ValueEnumType<T> = ValueEnumType<T>
> = Omit<EnumMap<T, V>, "set" | "clear" | "delete">;

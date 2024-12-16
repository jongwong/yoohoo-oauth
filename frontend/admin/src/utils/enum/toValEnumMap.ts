import type {BaseValueType, ExtraValueType, ValueEnumType} from './types';
import type {ReadonlyEnumMap} from './EnumMap';
import {EnumMap} from './EnumMap';

/**
 * 创建枚举toValEnumMap
 * @param list ReadonlyArray
 * @param fn
 * @returns
 */
export function toValEnumMap<T = any, P extends Record<string, any> = Record<string, any>, V extends ValueEnumType<T, P> = ValueEnumType<T, P>>(
    list: ReadonlyArray<V & ExtraValueType>,
    fn?: (val: V) => BaseValueType<T>
): ReadonlyEnumMap<T, V> {
    const map = new EnumMap<T, V>(
        list.map(item => {
            if (fn) {
                const newVal = fn(item as V);
                return [newVal.value, newVal];
            }
            return [item.value, item];
        }) as [V['value'], V][]
    );
    return map as unknown as T extends any ? ReadonlyEnumMap<V['value'], V> : ReadonlyEnumMap<T, V>;
}

import Big from 'big.js';
import { isNumber } from 'lodash';

// 判断值是否为有效数字，且不能为 NaN 和 Infinity
function isValidNumber(value: any): boolean {
	return typeof value === 'number' && !isNaN(value) && isFinite(value);
}

// 将值转换为 Big 类型的数字，支持 undefined 和 null 转换为 0，并且对无效值返回 undefined
function toBig(value: number): Big | undefined {
	return new Big(value); // 否则转换为 Big 类型
}

// 加法：支持多个数，返回 number 类型，且输入值必须是有效的非零数字
export function add(...numbers: (number | undefined | null)[]): number | undefined {
	if (numbers.some(num => !isNumber(num))) {
		return undefined; // 如果有无效值或除数为 0，返回 undefined
	}

	const _numbers = numbers as number[];

	return _numbers.reduce((acc, num) => acc?.plus(num)!, toBig(_numbers[0])!)?.toNumber?.(); // 最后转换为 number 类型
}

// 减法：支持多个数，依次相减，返回 number 类型，且输入值必须是有效的非零数字
export function subtract(...numbers: (number | undefined | null)[]): number | undefined {
	if (numbers.some(num => !isNumber(num))) {
		return undefined; // 如果有无效值或除数为 0，返回 undefined
	}

	const _numbers = numbers as number[];

	return _numbers
		.slice(1)
		.reduce((acc, num) => acc?.minus(num)!, toBig(_numbers[0])!)
		?.toNumber(); // 最后转换为 number 类型
}

// 乘法：支持多个数，返回 number 类型，且输入值必须是有效的非零数字
export function multiply(...numbers: (number | undefined | null)[]): number | undefined {
	if (numbers.some(num => !isNumber(num))) {
		return undefined; // 如果有无效值或除数为 0，返回 undefined
	}

	const _numbers = numbers as number[];

	return _numbers.reduce((acc, num) => acc?.mul(num)!, toBig(1)!).toNumber(); // 最后转换为 number 类型
}

// 除法：支持多个数，依次相除，返回 number 类型，且输入值必须是有效的非零数字，特别注意不能为 0
export function divide(...numbers: (number | undefined | null)[]): number | undefined {
	// 检查所有输入值，特别是第一个值不能为 0
	if (numbers.some(num => !isNumber(num))) {
		return undefined; // 如果有无效值或除数为 0，返回 undefined
	}
	const _numbers = numbers as number[];

	return _numbers
		.slice(1)
		.reduce((acc, num) => acc?.div(num)!, toBig(_numbers[0])!)
		?.toNumber(); // 最后转换为 number 类型
}

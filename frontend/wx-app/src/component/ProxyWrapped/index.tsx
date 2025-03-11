import type React from "react";

import { omit } from "lodash";

type ReturnType = React.ReactElement;

export type ProxyWrappedProps<T = any> = {
  value?: any;
  onChange?: (args: any[]) => any;
  children: (config: {
    value: any;
    onChange?: (e: any) => any;
    [key: string]: any;
  }) => ReturnType;
};

const ProxyWrapped: <T = any>(props: ProxyWrappedProps<T>) => ReturnType = (
  props
) => {
  const { value = undefined, onChange = () => null, ...rest } = props;
  const realProps = omit(rest, ["render"]);

  const newProps: any = { ...rest };

  return (
    <>
      {props?.children(
        omit({ value, onChange, ...newProps }, ["children"]) as any
      )}
    </>
  );
};
export default ProxyWrapped;

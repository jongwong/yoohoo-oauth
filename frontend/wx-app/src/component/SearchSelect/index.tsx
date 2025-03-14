import React, { ReactNode, useEffect, useRef, useState } from "react";
import { Cell, Empty, Popup, Search } from "@antmjs/vantui";
import { Input, ScrollView, View } from "@tarojs/components";
import { useDebounceFn } from "ahooks";

const DEFAULT_PAGE_SIZE = 10;

export type SearchSelectProps<T = any> = {
  request: (
    params: {
      page: number;
      size: number;
    } & Record<string, any>
  ) => Promise<{ data: any[]; total: number }>;
  params?: Record<string, any>;
  loadInitialOptions?: (keys: string[]) => Promise<any[]>;
  triggerLength?: number; // 输入多少字符后开始查询
  triggerMode?: "init" | "open" | "search";
  fieldNames?: Record<string, any>;
  value?: string;
  onChange?: (value: any) => void;
  searchKeyword?: string;
  optionRender?: (item: any) => React.ReactNode;
  renderInput?: (opt: { open: () => void }) => ReactNode;
};

const SearchSelect: React.FC<SearchSelectProps> = ({
  request,
  renderInput,
  params = {},
  loadInitialOptions,
  triggerMode = "open",
  searchKeyword = "name",
  optionRender,
  fieldNames = { label: "label", value: "value", key: "key" },
  value,
  onChange,
}) => {
  const [searchString, _setSearchString] = useState("");
  const searchStringRef = useRef("");
  const [data, setData] = useState<any[]>([]);
  const [selected, setSelected] = useState<any>(null);
  const [total, setTotal] = useState(-1);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [popupVisible, setPopupVisible] = useState(false);
  const hasMore = useRef(true);
  const initialized = useRef(false);
  // **回显已选数据**
  useEffect(() => {
    if (!value || !loadInitialOptions) return;
    if (Array.isArray(value) ? value.length === 0 : !value) return;

    const keys = Array.isArray(value) ? value : [value];
    loadInitialOptions(keys).then((result) => {
      if (result.length) {
        setData((prev) => [...result, ...prev]);
        setSelected(result[0]);
      }
    });
  }, [value]);

  const setSearchString = (val: string) => {
    searchStringRef.current = val;
    _setSearchString(val);
  };

  // **请求数据**
  const fetchData = async (reset = false) => {
    if (loading || (!reset && !hasMore.current)) return;

    setLoading(true);
    try {
      const _page = reset ? 1 : page;
      const response = await request({
        ...params,
        page: _page,
        size: DEFAULT_PAGE_SIZE,
        [searchKeyword]: searchStringRef.current || undefined,
      });
      setData(reset ? response.data : [...data, ...response.data]);
      setTotal(response.total);
      setPage(_page + 1);
      hasMore.current = _page * DEFAULT_PAGE_SIZE < response.total;
    } finally {
      setLoading(false);
    }
  };
  const { run: handleSearch } = useDebounceFn(
    (val) => {
      setSearchString(val);
      setPage(1);
      fetchData(true);
    },
    { wait: 300 }
  );

  const initFetch = () => {
    if (triggerMode === "init" && !initialized.current) {
      initialized.current = true;
      fetchData(true);
    }
  };
  // **触发模式**
  useEffect(() => {
    initFetch();
  }, []);

  // **滚动加载**
  const handleScroll = (event) => {
    const { scrollTop, scrollHeight, clientHeight } = event.detail;
    if (scrollTop + clientHeight >= scrollHeight - 50) {
      fetchData(false);
    }
  };
  const open = () => {
    if (triggerMode === "open") {
      if (!initialized.current) {
        initialized.current = true;
        fetchData(true);
      }
    }
    setPopupVisible(true);
  };
  return (
    <>
      {renderInput ? (
        renderInput({ open })
      ) : (
        <Input
          value={selected ? selected[fieldNames.label] : ""}
          placeholder="请选择"
          onClick={() => open()}
        />
      )}
      <Popup
        show={popupVisible}
        onClose={() => setPopupVisible(false)}
        round
        position="bottom"
        style={{ height: "80vh" }}
      >
        {/* 顶部搜索框 - 固定 */}
        <View
          style={{
            position: "sticky",
            top: 0,
            backgroundColor: "#fff",
            zIndex: 10,
          }}
        >
          <Search
            placeholder="请输入关键字"
            value={searchString}
            onChange={(e) => {
              handleSearch(e.detail);
            }}
          />
        </View>

        <View>
          {/* 可滚动内容区域 */}
          <ScrollView scrollY onScroll={handleScroll}>
            {data.length ? (
              data.map((item) => (
                <Cell
                  key={item[fieldNames.key]}
                  title={
                    optionRender ? optionRender(item) : item[fieldNames.label]
                  }
                  onClick={() => {
                    setSelected(item);
                    onChange?.(item);
                    setPopupVisible(false);
                  }}
                />
              ))
            ) : (
              <Empty description="暂无数据" />
            )}
          </ScrollView>
        </View>
      </Popup>
    </>
  );
};

export default SearchSelect;

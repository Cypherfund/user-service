package com.cypherfund.campaign.user.proxies;

import com.cypherfund.campaign.user.model.tiktok.TikTokVideoFilter;
import com.cypherfund.campaign.user.model.tiktok.TiktokVideoListResponse;
import com.cypherfund.campaign.user.model.tiktok.VideoListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tiktokClient", url = "https://open.tiktokapis.com")
public interface TikTokFeignClient {

    @PostMapping(value = "/v2/video/list/", consumes = "application/json", produces = "application/json")
    TiktokVideoListResponse getVideoList(@RequestHeader("Authorization") String authorization,
                                         @RequestParam("fields") String fields,
                                         @RequestBody VideoListRequest request);

    @PostMapping(value = "/v2/video/query/", consumes = "application/json", produces = "application/json")
    TiktokVideoListResponse queryVideoList(@RequestHeader("Authorization") String authorization,
                                           @RequestParam("fields") String fields,
                                           @RequestBody TikTokVideoFilter request);

}

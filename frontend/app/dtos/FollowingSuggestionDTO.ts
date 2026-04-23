import type UserBasicInfoDTO from "./UserBasicInfoDTO";

export default interface FollowingSuggestionDTO{
    suggestion: UserBasicInfoDTO,
    contact: UserBasicInfoDTO[],
    commonCount: number
}
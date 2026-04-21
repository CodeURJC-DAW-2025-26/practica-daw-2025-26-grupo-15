import type UserBasicInfoDTO from "./UserBasicInfoDTO";

export interface CommentDTO {
    id: number,
    text: string,
    lastUpdate: string,
    owner: UserBasicInfoDTO
}
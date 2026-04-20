import type UserBasicInfoDTO from "./UserBasicInfoDTO"

export interface SolutionBasicInfoDTO {
    id: number,
    name: string,
    owner: UserBasicInfoDTO,
    numComments: number,
    lastUpdate: Date
}
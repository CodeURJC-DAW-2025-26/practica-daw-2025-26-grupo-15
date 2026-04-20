
import type { ImageDTO } from "./ImageDTO";
import type UserBasicInfoDTO from "./UserBasicInfoDTO";

export interface UserDTO{
    id: number;
    name: string;
    email: string;
    photo: ImageDTO | null;
    followers: UserBasicInfoDTO[];
    following: UserBasicInfoDTO[];

    
}
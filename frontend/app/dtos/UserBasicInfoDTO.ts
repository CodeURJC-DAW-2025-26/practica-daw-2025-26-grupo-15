import type { ImageDTO } from "./ImageDTO";



export default interface UserBasicInfoDTO {
    id: number;
    name: string;
    email: string;
    photo: ImageDTO | null;
}
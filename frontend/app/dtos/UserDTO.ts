import type ExerciseListBasicInfoDTO from "./ExerciseListBasicInfoDTO";
import type ImageDTO from "./ImageDto";
import type UserBasicInfoDTO from "./UserBasicInfoDTO";

export interface UserDTO {
  id: number;
  name: string;
  email: string;
  bio: string;
  specialty: string;
  roles: string[];
  exerciseLists: ExerciseListBasicInfoDTO[];
  followers: UserBasicInfoDTO[];
  following: UserBasicInfoDTO[];
  requestedFriends: UserBasicInfoDTO[];
  requestReceived: UserBasicInfoDTO[];
  photo: ImageDTO;
}

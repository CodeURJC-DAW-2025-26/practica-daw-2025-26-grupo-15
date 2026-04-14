import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
	layout("routes/home.tsx", [
		index("routes/fyp.tsx"),
		route("users/:id", "routes/profile.tsx"),
		route("lists/:id", "routes/list-view.tsx"),
	]),
] satisfies RouteConfig;

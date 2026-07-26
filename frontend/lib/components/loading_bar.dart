import 'package:flutter/material.dart';
import '../../constants/app_colors.dart';

class LoadingBar extends StatefulWidget {
  final double width;
  final double height;
  final Duration duration;
  final Color? progressColor;
  final Color? backgroundColor;

  const LoadingBar({
    super.key,
    required this.width,
    this.height = 16,
    this.duration = const Duration(seconds: 3),
    this.progressColor,
    this.backgroundColor,
  });

  @override
  State<LoadingBar> createState() => _LoadingBarState();
}

class _LoadingBarState extends State<LoadingBar>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.duration,
      vsync: this,
    );
    _animation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: Curves.easeInOut,
      ),
    );
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
  final resolvedProgressColor  = AppColors.primaryTeal(context);
  final resolvedBackGroundColor =  AppColors.surfaceGrey(context);
    return AnimatedBuilder(
      animation: _animation,
      builder: (context, child) {
        return Container(
          width: widget.width,
          height: widget.height,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(8),
            color: resolvedProgressColor,
          ),
          child: Align(
            alignment: Alignment.centerLeft,
            child: Container(
              width: widget.width * _animation.value,
              height: widget.height,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                color: resolvedProgressColor,
              ),
            ),
          ),
        );
      },
    );
  }
}